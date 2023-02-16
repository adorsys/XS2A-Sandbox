/*
 * Copyright 2018-2023 adorsys GmbH & Co KG
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

import { Component, ViewEncapsulation } from '@angular/core';
import {
  trigger,
  state,
  style,
  transition,
  animate,
} from '@angular/animations';
import { Subject } from 'rxjs';

import { InfoOptions } from './info-options';

@Component({
  selector: 'app-feedback',
  templateUrl: './info.component.html',
  styleUrls: ['./info.component.scss'],
  animations: [
    trigger('feedbackAnimation', [
      state(
        'void',
        style({
          transform: 'translateY(100%)',
          opacity: 0,
        })
      ),
      state(
        '*',
        style({
          transform: 'translateY(0)',
          opacity: 1,
        })
      ),
      transition('* <=> void', animate(`400ms cubic-bezier(0.4, 0, 0.1, 1)`)),
    ]),
  ],
  encapsulation: ViewEncapsulation.None,
})
export class InfoComponent {
  private onDestroy = new Subject<void>();
  private durationTimeoutId: any;

  onDestroy$ = this.onDestroy.asObservable();
  message: string;
  options: InfoOptions;
  animationState: '*' | 'void' = 'void';

  open(message: string, options: InfoOptions): void {
    this.message = message;
    this.options = options;
    this.animationState = '*';
  }

  animateClose(): void {
    this.animationState = 'void';
    clearTimeout(this.durationTimeoutId);
  }

  /**
   * This is called after the animation is done by Angular
   * The state decides whether the component should be destroyed or not
   */
  animationDone() {
    if (this.animationState === 'void') {
      this.onDestroy.next();
    } else if (this.animationState === '*') {
      if (this.options) {
        this.dismissAfter(this.options.duration);
      }
    }
  }

  private dismissAfter(duration: number): void {
    if (duration && duration > 0) {
      this.durationTimeoutId = setTimeout(() => this.animateClose(), duration);
    }
  }
}
