import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TestValuesComponent } from './test-values.component';
import {Pipe, PipeTransform} from '@angular/core';

describe('TestValuesComponent', () => {
  let component: TestValuesComponent;
  let fixture: ComponentFixture<TestValuesComponent>;

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
        TestValuesComponent,
        TranslatePipe
      ],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TestValuesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
