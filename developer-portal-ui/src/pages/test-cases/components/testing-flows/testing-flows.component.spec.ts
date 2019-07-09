import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TestingFlowsComponent } from './testing-flows.component';
import {Pipe, PipeTransform} from '@angular/core';

describe('TestingFlowsComponent', () => {
  let component: TestingFlowsComponent;
  let fixture: ComponentFixture<TestingFlowsComponent>;

  @Pipe({name: 'translate'})
  class TranslatePipe implements PipeTransform {
    transform(value) {
      const tmp = value.split('.');
      return tmp[1];
    }
  }

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        TestingFlowsComponent,
        TranslatePipe
      ],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TestingFlowsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
