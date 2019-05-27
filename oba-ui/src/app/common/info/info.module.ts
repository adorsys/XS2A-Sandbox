import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';

import {InfoComponent} from './info.component';
import {OverlayModule} from "@angular/cdk/overlay";

@NgModule({
  imports: [
    CommonModule,
    OverlayModule
  ],
  declarations: [InfoComponent],
  entryComponents: [InfoComponent]
})
export class InfoModule {
}
