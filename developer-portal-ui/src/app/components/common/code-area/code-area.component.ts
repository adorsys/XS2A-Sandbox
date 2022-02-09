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

import { Component, Input } from '@angular/core';
import { DataService } from '../../../services/data.service';
import { JSON_SPACING } from '../constant/constants';

@Component({
  selector: 'app-code-area',
  templateUrl: './code-area.component.html',
  styleUrls: ['./code-area.component.scss'],
})
export class CodeAreaComponent {
  @Input() json: object;
  @Input() id: string;
  shown = false;

  constructor(private dataService: DataService) {}

  collapseThis(collapseId: string) {
    const collapsibleItemContent = document.getElementById(collapseId);
    if (collapseId === this.id) {
      this.shown = !this.shown;
    }
    if (collapsibleItemContent.style.maxHeight) {
      collapsibleItemContent.style.maxHeight = '';
    } else {
      collapsibleItemContent.style.maxHeight = `${collapsibleItemContent.scrollHeight}px`;
    }
  }

  copyText(json: object) {
    const textToCopy = JSON.stringify(json, null, JSON_SPACING);
    const selBox = document.createElement('textarea');
    selBox.style.position = 'fixed';
    selBox.style.left = '0';
    selBox.style.top = '0';
    selBox.style.opacity = '0';
    selBox.value = textToCopy;
    document.body.appendChild(selBox);
    selBox.focus();
    selBox.select();
    document.execCommand('copy');
    document.body.removeChild(selBox);
    this.dataService.showToast('Json body is copied to clipboard.', 'Copied successfully!', 'success');
  }
}
