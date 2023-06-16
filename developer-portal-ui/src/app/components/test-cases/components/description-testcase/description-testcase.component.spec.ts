import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DescriptionTestcaseComponent } from './description-testcase.component';
import { TranslateLoader, TranslateModule, TranslateService } from '@ngx-translate/core';
import { HttpLoaderFactory, LanguageService } from '../../../../services/language.service';
import { HttpClient } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { NO_ERRORS_SCHEMA } from '@angular/core';

describe('DescriptionTestcaseComponent', () => {
  let component: DescriptionTestcaseComponent;
  let fixture: ComponentFixture<DescriptionTestcaseComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [DescriptionTestcaseComponent],
      schemas: [NO_ERRORS_SCHEMA],
      imports: [
        TranslateModule.forRoot({
          loader: {
            provide: TranslateLoader,
            useFactory: HttpLoaderFactory,
            deps: [HttpClient],
          },
        }),
        HttpClientTestingModule,
      ],
      providers: [LanguageService, TranslateService],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DescriptionTestcaseComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
