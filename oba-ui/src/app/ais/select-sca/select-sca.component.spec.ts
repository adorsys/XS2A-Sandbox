import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {SelectScaComponent} from './select-sca.component';
import {RouterTestingModule} from "@angular/router/testing";
import {AccountDetailsComponent} from "../account-details/account-details.component";
import {ReactiveFormsModule} from "@angular/forms";

describe('SelectScaComponent', () => {
  let component: SelectScaComponent;
  let fixture: ComponentFixture<SelectScaComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule, ReactiveFormsModule],
      declarations: [SelectScaComponent, AccountDetailsComponent]
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
