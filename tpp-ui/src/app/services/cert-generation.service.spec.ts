import {inject, TestBed} from '@angular/core/testing';
import {HttpClientModule} from "@angular/common/http";
import {HttpClientTestingModule, HttpTestingController} from '@angular/common/http/testing';
import {CertGenerationService} from './cert-generation.service';
import {environment} from "../../environments/environment";

describe('CertGenerationService', () => {
    let httpMock: HttpTestingController;
    let certGenerationService: CertGenerationService;
    const url = `${environment.certificateGenerationServer}`;
    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [
                HttpClientModule, HttpClientTestingModule
            ],
            providers: [CertGenerationService]
        });
        httpMock = TestBed.get(HttpTestingController);
        certGenerationService = TestBed.get(CertGenerationService);
    });

    it('should be created', inject([CertGenerationService], (service: CertGenerationService) => {
        expect(service).toBeTruthy();
    }));

    it('should load the generate', () => {
        let mockCertData: any = {
        }
        certGenerationService.generate(mockCertData).subscribe((data: any) => {
            expect(data).toBe('')});;
        const req = httpMock.expectOne(url + '/api/cert-generator', mockCertData);
        expect(req.request.method).toBe('POST');
        httpMock.verify();
    });
});
