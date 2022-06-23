import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FundsConfirmationNavComponent } from './funds-confirmation-nav.component';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { HttpLoaderFactory } from '../../../../services/language.service';
import { HttpClient } from '@angular/common/http';

describe('FundsConfirmationNavComponent', () => {
  let component: FundsConfirmationNavComponent;
  let fixture: ComponentFixture<FundsConfirmationNavComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [FundsConfirmationNavComponent],
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
    fixture = TestBed.createComponent(FundsConfirmationNavComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
