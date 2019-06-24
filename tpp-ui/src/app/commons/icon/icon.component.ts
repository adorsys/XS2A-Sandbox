import {
  ChangeDetectionStrategy,
  Component,
  Input,
  ViewEncapsulation,
  ElementRef,
  SimpleChanges,
  OnChanges
} from '@angular/core';
import { DomSanitizer } from '@angular/platform-browser';
import { take } from 'rxjs/operators';
import { IconRegistry } from './icon-registry';

@Component({
  selector: 'app-icon',
  template: '<ng-content></ng-content>',
  styleUrls: ['./icon.component.scss'],
  host: {
    class: 'app-icon',
    '[class.app-icon--inline]': 'inline'
  },
  encapsulation: ViewEncapsulation.None,
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class IconComponent implements OnChanges {
  /**
   * Whether the icon should be inlined, automatically sizing the icon to match the font size of
   * the element the icon is contained in.
   */
  @Input()
  inline = false;

  /** Name of the icon in the SVG icon set. */
  @Input()
  svgIcon: string;

  constructor(
    private _elementRef: ElementRef<HTMLElement>,
    private _iconRegistry: IconRegistry,
    private _sanitizer: DomSanitizer
  ) {
    const icons = ['user', 'account', 'upload', 'euro', 'add', 'generate_test_data'];
    icons.forEach(val => {
      _iconRegistry.addSvgIcon(
          val,
          _sanitizer.bypassSecurityTrustResourceUrl('assets/icons/'+val+'.svg')
      );
    });
  }

  private _splitIconName(iconName: string): [string, string] {
    if (!iconName) {
      return ['', ''];
    }
    const parts = iconName.split(':');
    switch (parts.length) {
      case 1:
        return ['', parts[0]]; // Use default namespace.
      case 2:
        return <[string, string]>parts;
      default:
        throw Error(`Invalid icon name: "${iconName}"`);
    }
  }

  ngOnChanges(changes: SimpleChanges): void {
    // Only update the inline SVG icon if the inputs changed, to avoid unnecessary DOM operations.
    if (changes['svgIcon']) {
      if (this.svgIcon) {
        const [namespace, iconName] = this._splitIconName(this.svgIcon);

        this._iconRegistry
          .getNamedSvgIcon(iconName, namespace)
          .pipe(take(1))
          .subscribe(
            svg => this._setSvgElement(svg),
            (err: Error) => console.error(`Error retrieving icon: ${err.message}`)
          );
      } else {
        this._clearSvgElement();
      }
    }
  }

  private _setSvgElement(svg: SVGElement) {
    this._clearSvgElement();

    // Workaround for IE11 and Edge ignoring `style` tags inside dynamically-created SVGs.
    // See: https://developer.microsoft.com/en-us/microsoft-edge/platform/issues/10898469/
    // Do this before inserting the element into the DOM, in order to avoid a style recalculation.
    const styleTags = svg.querySelectorAll('style') as NodeListOf<HTMLStyleElement>;

    for (let i = 0; i < styleTags.length; i++) {
      styleTags[i].textContent += ' ';
    }

    this._elementRef.nativeElement.appendChild(svg);
  }

  private _clearSvgElement() {
    const layoutElement: HTMLElement = this._elementRef.nativeElement;
    let childCount = layoutElement.childNodes.length;

    // Remove existing non-element child nodes and SVGs, and add the new SVG element. Note that
    // we can't use innerHTML, because IE will throw if the element has a data binding.
    while (childCount--) {
      const child = layoutElement.childNodes[childCount];

      // 1 corresponds to Node.ELEMENT_NODE. We remove all non-element nodes in order to get rid
      // of any loose text nodes, as well as any SVG elements in order to remove any old icons.
      if (child.nodeType !== 1 || child.nodeName.toLowerCase() === 'svg') {
        layoutElement.removeChild(child);
      }
    }
  }
}
