/*
 * Copyright 2018-2024 adorsys GmbH & Co KG
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version. This program is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see https://www.gnu.org/licenses/.
 *
 * This project is also available under a separate commercial license. You can
 * contact us at sales@adorsys.com.
 */

import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
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
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { HttpLoaderFactory } from '../../../../services/language.service';

describe('PlayWthDataComponent', () => {
  let component: PlayWthDataComponent;
  let fixture: ComponentFixture<PlayWthDataComponent>;

  const DataServiceStub = {
    setIsLoading: () => {},
    getIsLoading: () => false,
  };

  const RestServiceStub = {
    sendRequest: () => {},
  };

  const JsonServiceStub = {
    getPreparedJsonData: () => {
      return '{body: body}';
    },
  };

  const CopyServiceStub = {
    getCopyValue: () => {},
    copyThis: () => {},
  };

  const LocalStorageServiceStub = {};

  @Pipe({ name: 'translate' })
  class TranslatePipe implements PipeTransform {
    transform(value) {
      const tmp = value.split('.');
      return tmp[1];
    }
  }

  beforeEach(
    waitForAsync(() => {
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
            },
          }),
        ],
        providers: [
          { provide: DataService, useValue: DataServiceStub },
          { provide: RestService, useValue: RestServiceStub },
          { provide: CopyService, useValue: CopyServiceStub },
          {
            provide: LocalStorageService,
            useValue: LocalStorageServiceStub,
          },
          { provide: JsonService, useValue: JsonServiceStub },
        ],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(PlayWthDataComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
