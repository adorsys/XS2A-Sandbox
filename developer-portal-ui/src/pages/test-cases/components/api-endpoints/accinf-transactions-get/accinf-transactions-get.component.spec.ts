import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AccinfTransactionsGetComponent } from './accinf-transactions-get.component';
import {Component, Input, Pipe, PipeTransform} from '@angular/core';
import {LineCommandComponent} from '../../../../../custom-elements/line-command/line-command.component';

describe('AccinfTransactionsGetComponent', () => {
  let component: AccinfTransactionsGetComponent;
  let fixture: ComponentFixture<AccinfTransactionsGetComponent>;

  @Component({
    selector: 'app-play-wth-data',
    template: ''
  })
  class MockPlayWithDataComponent {
    @Input() headers: object;
    @Input() accountIdFlag: boolean;
    @Input() bookingStatusFlag: boolean;
    @Input() variablePathEnd: string;
  }

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
        AccinfTransactionsGetComponent,
        LineCommandComponent,
        TranslatePipe,
        MockPlayWithDataComponent
      ]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AccinfTransactionsGetComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create (not all)', () => {
    expect(component).toBeTruthy();
  });
});
