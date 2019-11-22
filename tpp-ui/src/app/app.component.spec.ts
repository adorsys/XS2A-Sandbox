import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { NgHttpLoaderModule } from 'ng-http-loader';
import { of } from 'rxjs';

import { AppComponent } from './app.component';
import { CustomizeService } from './services/customize.service';

describe('AppComponent', () => {
    let component: AppComponent;
    let fixture: ComponentFixture<AppComponent>;
    let customizeService: CustomizeService;

    const CustomizeServiceStub = {
        isCustom: () => false,
        setUserTheme: () => {},
        getJSON: () => {
            return of({
                globalSettings: {
                    logo: '../assets/UI/Logo_XS2ASandbox.png',
                    cssVariables: {
                        colorPrimary: '#054f72',
                        fontFamily: 'Arial, sans-serif',
                        headerBG: '#ffffff',
                        headerFontColor: '#000000'
                    }
                }
            });
        },
        getLogo: () => '../assets/UI/Logo_XS2ASandbox.png',
    };

    beforeEach(async(() => {
        TestBed.configureTestingModule({
            imports: [
                NgHttpLoaderModule,
                RouterTestingModule,
            ],
            declarations: [
                AppComponent
            ],
            providers: [
                { provide: CustomizeService, useValue: CustomizeServiceStub },
            ]
        })
        .compileComponents()
        .then(() => {
            customizeService = TestBed.get(CustomizeService);
        });
    }));

    beforeEach(() => {
        fixture = TestBed.createComponent(AppComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });

    it('should set global settings in ngOnInit', () => {
        const getGlobalSettingsSpy = spyOn(
            customizeService,
            'getJSON'
        ).and.callThrough();

        component.ngOnInit();

        expect(getGlobalSettingsSpy).toHaveBeenCalled();
        expect(component.globalSettings).not.toBeUndefined();
    });
});
