import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ScaSelectionComponent } from './sca-selection.component';

describe('ScaSelectionComponent', () => {
  let component: ScaSelectionComponent;
  let fixture: ComponentFixture<ScaSelectionComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ScaSelectionComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ScaSelectionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('sca selection component created', () => {
    expect(component).toBeTruthy();
  });
});
