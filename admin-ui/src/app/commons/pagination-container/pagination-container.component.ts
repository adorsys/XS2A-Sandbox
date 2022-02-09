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

import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { NgbPaginationConfig } from '@ng-bootstrap/ng-bootstrap';
import {
  PaginationConfigModel,
  PageConfig,
} from '../../models/pagination-config.model';

@Component({
  selector: 'app-pagination-container',
  templateUrl: './pagination-container.component.html',
  styleUrls: ['./pagination-container.component.scss'],
})
export class PaginationContainerComponent implements OnInit {
  @Output() pageDataConfig = new EventEmitter<PageConfig>();
  @Input() collectionSize: number;
  @Input() paginationConfig: PaginationConfigModel;

  constructor(private config: NgbPaginationConfig) {
    // customize default values of paginations used by this component tree
    config.size = 'sm';
    config.boundaryLinks = true;
    config.maxSize = 7;
  }

  ngOnInit() {}

  pageChange(pageNumber: number) {
    this.paginationConfig.currentPageNumber = pageNumber;
    this.emitCurrentPageConfig();
  }

  pageSizeChange(pageSize) {
    if (pageSize) {
      this.paginationConfig.itemsPerPage = pageSize;
    }
    this.emitCurrentPageConfig();
  }

  emitCurrentPageConfig() {
    const pageConfig: PageConfig = {
      pageNumber: this.paginationConfig.currentPageNumber,
      pageSize: this.paginationConfig.itemsPerPage,
    };
    this.pageDataConfig.emit(pageConfig);
  }
}
