import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import {PaginationConfigModel, PageConfig} from "../../models/pagination-config.model";
import { PaginationContainerComponent } from './pagination-container.component';
import {NgbPaginationModule} from "@ng-bootstrap/ng-bootstrap";
import {FormsModule} from "@angular/forms";

describe('PaginationContainerComponent', () => {
  let component: PaginationContainerComponent;
  let fixture: ComponentFixture<PaginationContainerComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ PaginationContainerComponent ],
      imports: [
          NgbPaginationModule,
          FormsModule]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PaginationContainerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should change the page', () => {
      const paginationConfigModel: PaginationConfigModel = {
          itemsPerPage: 0,
          currentPageNumber: 0,
          totalItems: 0
      }
      component.paginationConfig = paginationConfigModel;
      component.pageChange(15);
      expect(component.paginationConfig.currentPageNumber).toEqual(15);
  });

  it('should change the page size', () => {
      const paginationConfigModel: PaginationConfigModel = {
          itemsPerPage: 0,
          currentPageNumber: 0,
          totalItems: 0
      }
      component.paginationConfig = paginationConfigModel;
      component.pageSizeChange(10);
      expect(component.paginationConfig.itemsPerPage).toEqual(10);
  });

  it('should call emit page, when page size is not set', () => {
    const paginationConfigModel: PaginationConfigModel = {
        itemsPerPage: 8,
        currentPageNumber: 0,
        totalItems: 0
    }
      component.paginationConfig = paginationConfigModel;
    component.pageSizeChange(null);
    expect(component.paginationConfig.itemsPerPage).toEqual(8);
  });
    it('should call the emitCurrentPageConfig', () => {
        const pageDataConfigSpy  = spyOn(component.pageDataConfig, 'emit');
        const expected = {
          pageNumber: 6,
            pageSize: 10
        };
        const paginationConfigModel: PaginationConfigModel = {
            itemsPerPage: 10,
            currentPageNumber: 6,
            totalItems: 100
        };
        component.paginationConfig = paginationConfigModel;
        component.emitCurrentPageConfig();
        expect(pageDataConfigSpy).toHaveBeenCalledWith(expected);
    });
});
