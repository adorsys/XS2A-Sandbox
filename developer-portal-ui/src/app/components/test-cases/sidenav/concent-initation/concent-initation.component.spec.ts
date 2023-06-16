import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ConcentInitationComponent } from './concent-initation.component';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { HttpLoaderFactory } from '../../../../services/language.service';
import { HttpClient } from '@angular/common/http';
import { NO_ERRORS_SCHEMA } from '@angular/core';

describe('ConcentInitationComponentComponent', () => {
  let component: ConcentInitationComponent;
  let fixture: ComponentFixture<ConcentInitationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ConcentInitationComponent],
      schemas: [NO_ERRORS_SCHEMA],
      imports: [
        HttpClientTestingModule,
        TranslateModule.forRoot({
          loader: {
            provide: TranslateLoader,
            useFactory: HttpLoaderFactory,
            deps: [HttpClient],
          },
        }),
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ConcentInitationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
