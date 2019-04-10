import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SelectScaComponent } from './select-sca.component';

describe('SelectScaComponent', () => {
  let component: SelectScaComponent;
  let fixture: ComponentFixture<SelectScaComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SelectScaComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SelectScaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
