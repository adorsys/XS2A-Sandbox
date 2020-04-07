import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { NgxLoadingModule } from 'ngx-loading';
import { Pipe, PipeTransform } from '@angular/core';

import { PlayWthDataComponent } from './play-wth-data.component';
import { DataService } from '../../../../services/data.service';
import { FormsModule } from '@angular/forms';
import { RestService } from '../../../../services/rest.service';
import { CopyService } from '../../../../services/copy.service';
import { LocalStorageService } from '../../../../services/local-storage.service';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { JsonService } from '../../../../services/json.service';
import { PopUpComponent } from './pop-up/pop-up.component';
import {HttpClient, HttpClientModule} from '@angular/common/http';
import {TranslateLoader, TranslateModule} from "@ngx-translate/core";
import {HttpLoaderFactory} from "../../../../services/language.service";

describe('PlayWthDataComponent', () => {
  let component: PlayWthDataComponent;
  let fixture: ComponentFixture<PlayWthDataComponent>;

  const DataServiceStub = {
    setIsLoading: (val: boolean) => {},
    getIsLoading: () => false,
  };

  const RestServiceStub = {
    sendRequest: () => {},
  };

  const JsonServiceStub = {
    getPreparedJsonData: (val: string) => {
      return '{body: body}';
    },
  };

  const CopyServiceStub = {
    getCopyValue: (i, fieldsToCopy, response, paymentId) => {},
    copyThis: (i, fieldsToCopy) => {},
  };

  const LocalStorageServiceStub = {};

  @Pipe({ name: 'translate' })
  class TranslatePipe implements PipeTransform {
    transform(value) {
      const tmp = value.split('.');
      return tmp[1];
    }
  }

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [PlayWthDataComponent, PopUpComponent, TranslatePipe],
      imports: [
        FormsModule,
        NgxLoadingModule,
        HttpClientTestingModule,
        HttpClientModule,
        TranslateModule.forRoot({
          loader: {
            provide: TranslateLoader,
            useFactory: HttpLoaderFactory,
            deps: [HttpClient],
          }
        })
      ],
      providers: [
        { provide: DataService, useValue: DataServiceStub },
        { provide: RestService, useValue: RestServiceStub },
        { provide: CopyService, useValue: CopyServiceStub },
        { provide: LocalStorageService, useValue: LocalStorageServiceStub },
        { provide: JsonService, useValue: JsonServiceStub },
      ],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PlayWthDataComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
