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

import { Injectable } from '@angular/core';
import { MarkdownService } from 'ngx-markdown';

@Injectable({
  providedIn: 'root',
})
export class MarkdownStylingService {
  private counter = 1;

  constructor(private markdownService: MarkdownService) {}

  public resetCounter() {
    this.counter = 0;
  }

  public createTableOfContent(contentTable?: boolean) {
    this.markdownService.renderer.heading = (text: string, level: number) => {
      const headerTag = `<section id="${this.counter}"><h${level}> ${text} </h${level}></section>`;

      if (level === 1) {
        if (contentTable) {
          this.addHeaderToTable(text, this.counter);
        }
      }
      this.counter++;

      return headerTag;
    };
  }

  private addHeaderToTable(text: string, id: number) {
    const li = document.createElement('li');
    li.innerHTML = `<a href="/getting-started#${id}">${text}</a>`;

    const ol = document.getElementById('contentTable');
    if (ol) {
      ol.appendChild(li);
    }
  }
}
