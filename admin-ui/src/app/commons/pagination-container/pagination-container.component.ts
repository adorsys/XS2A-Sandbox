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
