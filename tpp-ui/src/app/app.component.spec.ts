import {TestBed, async, ComponentFixture} from '@angular/core/testing';
import {RouterTestingModule} from "@angular/router/testing";
import {NgHttpLoaderModule} from 'ng-http-loader';
import {AppComponent} from './app.component';

describe('AppComponent', () => {
    let component: AppComponent;
    let fixture: ComponentFixture<AppComponent>;

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

    beforeEach(() => {
        fixture = TestBed.createComponent(AppComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });
});
