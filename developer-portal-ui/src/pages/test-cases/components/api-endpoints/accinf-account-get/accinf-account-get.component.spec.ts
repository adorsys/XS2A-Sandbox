import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AccinfAccountGetComponent } from './accinf-account-get.component';

describe('AccinfAccountGetComponent', () => {
  let component: AccinfAccountGetComponent;
  let fixture: ComponentFixture<AccinfAccountGetComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AccinfAccountGetComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AccinfAccountGetComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
