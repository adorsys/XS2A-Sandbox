import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import {Component, Input, Pipe, PipeTransform} from '@angular/core';

import { EmbConsentGetComponent } from './emb-consent-get.component';
import {LineCommandComponent} from '../../../../../custom-elements/line-command/line-command.component';

describe('EmbConsentGetComponent', () => {
  let component: EmbConsentGetComponent;
  let fixture: ComponentFixture<EmbConsentGetComponent>;

  @Component({
    selector: 'app-play-wth-data',
    template: ''
  })
  class MockPlayWithDataComponent {
    @Input() headers: object;
    @Input() consentIdFlag: boolean;
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
        EmbConsentGetComponent,
        TranslatePipe,
        LineCommandComponent,
        MockPlayWithDataComponent
      ],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EmbConsentGetComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create (not all)', () => {
    expect(component).toBeTruthy();
  });
});
