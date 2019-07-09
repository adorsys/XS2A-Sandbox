import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AccinfAccountsGetComponent } from './accinf-accounts-get.component';
import {Component, Input, Pipe, PipeTransform} from '@angular/core';
import {LineCommandComponent} from '../../../../../custom-elements/line-command/line-command.component';

describe('AccinfAccountsGetComponent', () => {
  let component: AccinfAccountsGetComponent;
  let fixture: ComponentFixture<AccinfAccountsGetComponent>;

  @Component({
    selector: 'app-play-wth-data',
    template: ''
  })
  class MockPlayWithDataComponent {
    @Input() headers: object;
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
        AccinfAccountsGetComponent,
        TranslatePipe,
        MockPlayWithDataComponent,
        LineCommandComponent
      ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AccinfAccountsGetComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create (not all)', () => {
    expect(component).toBeTruthy();
  });
});
