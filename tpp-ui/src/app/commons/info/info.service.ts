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

import { Injectable } from '@angular/core';
import { ComponentPortal } from '@angular/cdk/portal';
import { Overlay, OverlayRef } from '@angular/cdk/overlay';

import { InfoModule } from './info.module';
import { InfoOptions } from './info-options';
import { InfoComponent } from './info.component';

@Injectable({
  providedIn: InfoModule,
})
export class InfoService {
  private overlayRef: OverlayRef;
  private feedbackComp: InfoComponent;
  private readonly CORNER_OFFSET = '20px';
  private readonly DEFAULT_OPTIONS: InfoOptions = {
    severity: 'info',
    closable: true,
    duration: 6000,
  };

  constructor(private overlay: Overlay) {
    this.overlayRef = this.overlay.create({
      hasBackdrop: false,
      scrollStrategy: this.overlay.scrollStrategies.noop(),
      positionStrategy: this.overlay
        .position()
        .global()
        .right(this.CORNER_OFFSET)
        .top(this.CORNER_OFFSET),
    });
  }

  openFeedback(message: string, options?: Partial<InfoOptions>) {
    if (this.overlayRef.hasAttached()) {
      this.overlayRef.detach();
    }
    const portal = new ComponentPortal(InfoComponent);
    const componentRef = this.overlayRef.attach(portal);
    this.feedbackComp = componentRef.instance;
    this.feedbackComp.open(message, { ...this.DEFAULT_OPTIONS, ...options });
    this.feedbackComp.onDestroy$.subscribe(() => {
      this.overlayRef.detach();
    });
  }
}
