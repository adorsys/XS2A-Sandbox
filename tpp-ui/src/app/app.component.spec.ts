import {TestBed, async} from '@angular/core/testing';
import {RouterTestingModule} from "@angular/router/testing";
import {NgHttpLoaderModule} from 'ng-http-loader';
import {AppComponent} from './app.component';

describe('AppComponent', () => {
    beforeEach(async(() => {
        TestBed.configureTestingModule({
            imports: [
                NgHttpLoaderModule,
                RouterTestingModule,
            ],
            declarations: [
                AppComponent
            ],
        }).compileComponents();
    }));
});
