/*
 * Copyright 2018-2022 adorsys GmbH & Co KG
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version. This program is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see https://www.gnu.org/licenses/.
 *
 * This project is also available under a separate commercial license. You can
 * contact us at psd2@adorsys.com.
 */

import { DOCUMENT } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import {
  Inject,
  Injectable,
  InjectionToken,
  Optional,
  SecurityContext,
  SkipSelf,
  OnDestroy,
} from '@angular/core';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import {
  Observable,
  of as observableOf,
  throwError as observableThrow,
} from 'rxjs';
import { finalize, map, share, tap } from 'rxjs/operators';

/**
 * Returns an exception to be thrown in the case when attempting to
 * load an icon with a name that cannot be found.
 * @docs-private
 */
export function getIconNameNotFoundError(iconName: string): Error {
  return Error(`Unable to find icon with the name "${iconName}"`);
}

/**
 * Returns an exception to be thrown when the consumer attempts to use
 * `<app-icon>` without including @angular/http.
 * @docs-private
 */
export function getIconNoHttpProviderError(): Error {
  return Error(
    'Could not find HttpClient provider. ' +
      'Please include the HttpClientModule from @angular/common/http in your ' +
      'app imports.'
  );
}

/**
 * Returns an exception to be thrown when a URL couldn't be sanitized.
 * @param url URL that was attempted to be sanitized.
 * @docs-private
 */
export function getIconFailedToSanitizeUrlError(url: SafeResourceUrl): Error {
  return Error(
    `The URL provided to KbnIconRegistry was not trusted as a resource URL ` +
      `via Angular's DomSanitizer. Attempted URL was "${url}".`
  );
}

/**
 * Configuration for an icon, including the URL and possibly the cached SVG element.
 * @docs-private
 */
class SvgIconConfig {
  url: SafeResourceUrl | null;
  svgElement: SVGElement | null;

  constructor(url: SafeResourceUrl);
  // tslint:disable-next-line:unified-signatures
  constructor(svgElement: SVGElement);
  constructor(data: SafeResourceUrl | SVGElement) {
    // Note that we can't use `instanceof SVGElement` here,
    // because it'll break during server-side rendering.
    if ((data as any).nodeName) {
      this.svgElement = data as SVGElement;
    } else {
      this.url = data as SafeResourceUrl;
    }
  }
}

/**
 * Service to register and display icons used by the `<app-icon>` component.
 * - Registers icon URLs by namespace and name.
 * - Registers icon set URLs by namespace.
 * - Loads icons from URLs and extracts individual icons from icon sets.
 */
@Injectable({ providedIn: 'root' })
export class IconRegistry implements OnDestroy {
  private _document: Document;

  /**
   * URLs and cached SVG elements for individual icons. Keys are of the format "[namespace]:[icon]".
   */
  private _svgIconConfigs = new Map<string, SvgIconConfig>();

  /** Cache for icons loaded by direct URLs. */
  private _cachedIconsByUrl = new Map<string, SVGElement>();

  /** In-progress icon fetches. Used to coalesce multiple requests to the same URL. */
  private _inProgressUrlFetches = new Map<string, Observable<string>>();

  constructor(
    @Optional() private _httpClient: HttpClient,
    private _sanitizer: DomSanitizer,
    @Optional()
    @Inject(DOCUMENT)
    document: any
  ) {
    this._document = document;
  }

  /**
   * Registers an icon by URL in the default namespace.
   * @param iconName Name under which the icon should be registered.
   * @param url
   */
  addSvgIcon(iconName: string, url: SafeResourceUrl): this {
    return this.addSvgIconInNamespace('', iconName, url);
  }

  /**
   * Registers an icon by URL in the specified namespace.
   * @param namespace Namespace in which the icon should be registered.
   * @param iconName Name under which the icon should be registered.
   * @param url
   */
  addSvgIconInNamespace(
    namespace: string,
    iconName: string,
    url: SafeResourceUrl
  ): this {
    return this._addSvgIconConfig(namespace, iconName, new SvgIconConfig(url));
  }

  /**
   * Returns an Observable that produces the icon (as an `<svg>` DOM element) from the given URL.
   * The response from the URL may be cached so this will not always cause an HTTP request, but
   * the produced element will always be a new copy of the originally fetched icon. (That is,
   * it will not contain any modifications made to elements previously returned).
   *
   * @param safeUrl URL from which to fetch the SVG icon.
   */
  getSvgIconFromUrl(safeUrl: SafeResourceUrl): Observable<SVGElement> {
    const url = this._sanitizer.sanitize(SecurityContext.RESOURCE_URL, safeUrl);

    if (!url) {
      throw getIconFailedToSanitizeUrlError(safeUrl);
    }

    const cachedIcon = this._cachedIconsByUrl.get(url);

    if (cachedIcon) {
      return observableOf(cloneSvg(cachedIcon));
    }

    return this._loadSvgIconFromConfig(new SvgIconConfig(safeUrl)).pipe(
      tap((svg) => this._cachedIconsByUrl.set(url, svg)),
      map((svg) => cloneSvg(svg))
    );
  }

  /**
   * Returns an Observable that produces the icon (as an `<svg>` DOM element) with the given name
   * and namespace. The icon must have been previously registered with addIcon;
   * if not, the Observable will throw an error.
   *
   * @param name Name of the icon to be retrieved.
   * @param namespace Namespace in which to look for the icon.
   */
  getNamedSvgIcon(name: string, namespace = ''): Observable<SVGElement> {
    // Return (copy of) cached icon if possible.
    const key = iconKey(namespace, name);
    const config = this._svgIconConfigs.get(key);

    if (config) {
      return this._getSvgFromConfig(config);
    }

    return observableThrow(getIconNameNotFoundError(key));
  }

  ngOnDestroy() {
    this._svgIconConfigs.clear();
    this._cachedIconsByUrl.clear();
  }

  /**
   * Returns the cached icon for a SvgIconConfig if available, or fetches it from its URL if not.
   */
  private _getSvgFromConfig(config: SvgIconConfig): Observable<SVGElement> {
    if (config.svgElement) {
      // We already have the SVG element for this icon, return a copy.
      return observableOf(cloneSvg(config.svgElement));
    } else {
      // Fetch the icon from the config's URL, cache it, and return a copy.
      return this._loadSvgIconFromConfig(config).pipe(
        tap((svg) => (config.svgElement = svg)),
        map((svg) => cloneSvg(svg))
      );
    }
  }

  /**
   * Loads the content of the icon URL specified in the SvgIconConfig and creates an SVG element
   * from it.
   */
  private _loadSvgIconFromConfig(
    config: SvgIconConfig
  ): Observable<SVGElement> {
    return this._fetchUrl(config.url).pipe(
      map((svgText) => this._createSvgElementForSingleIcon(svgText))
    );
  }

  /**
   * Creates a DOM element from the given SVG string, and adds default attributes.
   */
  private _createSvgElementForSingleIcon(responseText: string): SVGElement {
    const svg = this._svgElementFromString(responseText);
    this._setSvgAttributes(svg);
    return svg;
  }

  /**
   * Creates a DOM element from the given SVG string.
   */
  private _svgElementFromString(str: string): SVGElement {
    const div = this._document.createElement('DIV');
    div.innerHTML = str;
    const svg = div.querySelector('svg') as SVGElement;

    if (!svg) {
      throw Error('<svg> tag not found');
    }

    return svg;
  }

  /**
   * Sets the default attributes for an SVG element to be used as an icon.
   */
  private _setSvgAttributes(svg: SVGElement): SVGElement {
    svg.setAttribute('fit', '');
    svg.setAttribute('height', '100%');
    svg.setAttribute('width', '100%');
    svg.setAttribute('preserveAspectRatio', 'xMidYMid meet');
    svg.setAttribute('focusable', 'false'); // Disable IE11 default behavior to make SVGs focusable.
    return svg;
  }

  /**
   * Returns an Observable which produces the string contents of the given URL. Results may be
   * cached, so future calls with the same URL may not cause another HTTP request.
   */
  private _fetchUrl(safeUrl: SafeResourceUrl | null): Observable<string> {
    if (!this._httpClient) {
      throw getIconNoHttpProviderError();
    }

    if (safeUrl == null) {
      throw Error(`Cannot fetch icon from URL "${safeUrl}".`);
    }

    const url = this._sanitizer.sanitize(SecurityContext.RESOURCE_URL, safeUrl);

    if (!url) {
      throw getIconFailedToSanitizeUrlError(safeUrl);
    }

    // Store in-progress fetches to avoid sending a duplicate request for a URL when there is
    // already a request in progress for that URL. It's necessary to call share() on the
    // Observable returned by http.get() so that multiple subscribers don't cause multiple XHRs.
    const inProgressFetch = this._inProgressUrlFetches.get(url);

    if (inProgressFetch) {
      return inProgressFetch;
    }

    // Observable. Figure out why and fix it.
    const req = this._httpClient.get(url, { responseType: 'text' }).pipe(
      finalize(() => this._inProgressUrlFetches.delete(url)),
      share()
    );

    this._inProgressUrlFetches.set(url, req);
    return req;
  }

  /**
   * Registers an icon config by name in the specified namespace.
   * @param namespace Namespace in which to register the icon config.
   * @param iconName Name under which to register the config.
   * @param config Config to be registered.
   */
  private _addSvgIconConfig(
    namespace: string,
    iconName: string,
    config: SvgIconConfig
  ): this {
    this._svgIconConfigs.set(iconKey(namespace, iconName), config);
    return this;
  }
}

/** @docs-private */
export function ICON_REGISTRY_PROVIDER_FACTORY(
  parentRegistry: IconRegistry,
  httpClient: HttpClient,
  sanitizer: DomSanitizer,
  document?: any
) {
  return parentRegistry || new IconRegistry(httpClient, sanitizer, document);
}

/** @docs-private */
export const ICON_REGISTRY_PROVIDER = {
  // If there is already an KbnIconRegistry available, use that. Otherwise, provide a new one.
  provide: IconRegistry,
  deps: [
    [new Optional(), new SkipSelf(), IconRegistry],
    [new Optional(), HttpClient],
    DomSanitizer,
    [new Optional(), DOCUMENT as InjectionToken<any>],
  ],
  useFactory: ICON_REGISTRY_PROVIDER_FACTORY,
};

/** Clones an SVGElement while preserving type information. */
function cloneSvg(svg: SVGElement): SVGElement {
  return svg.cloneNode(true) as SVGElement;
}

/** Returns the cache key to use for an icon namespace and name. */
function iconKey(namespace: string, name: string) {
  return namespace + ':' + name;
}
