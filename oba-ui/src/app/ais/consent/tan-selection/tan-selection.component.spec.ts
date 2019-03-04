import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TanSelectionComponent } from './tan-selection.component';

describe('TanSelectionComponent', () => {
  let component: TanSelectionComponent;
  let fixture: ComponentFixture<TanSelectionComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ TanSelectionComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TanSelectionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
