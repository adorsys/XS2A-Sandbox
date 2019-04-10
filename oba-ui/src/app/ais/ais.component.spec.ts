import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AisComponent } from './ais.component';

describe('AisComponent', () => {
  let component: AisComponent;
  let fixture: ComponentFixture<AisComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AisComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AisComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
