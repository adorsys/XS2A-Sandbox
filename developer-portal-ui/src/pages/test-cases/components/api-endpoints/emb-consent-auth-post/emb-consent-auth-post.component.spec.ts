import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import {Component, Input, Pipe, PipeTransform} from '@angular/core';

import { EmbConsentAuthPostComponent } from './emb-consent-auth-post.component';
import {LineCommandComponent} from '../../../../../custom-elements/line-command/line-command.component';

describe('EmbConsentAuthPostComponent', () => {
  let component: EmbConsentAuthPostComponent;
  let fixture: ComponentFixture<EmbConsentAuthPostComponent>;

  @Component({
    selector: 'app-play-wth-data',
    template: ''
  })
  class MockPlayWithDataComponent {
    @Input() headers: object;
    @Input() consentIdFlag: boolean;
    @Input() variablePathEnd: string;
    @Input() fieldsToCopy: string[];
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
        EmbConsentAuthPostComponent,
        TranslatePipe,
        LineCommandComponent,
        MockPlayWithDataComponent
      ],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EmbConsentAuthPostComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create (not all)', () => {
    expect(component).toBeTruthy();
  });
});
