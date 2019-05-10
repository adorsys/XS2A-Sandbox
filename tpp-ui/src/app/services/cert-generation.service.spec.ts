import {inject, TestBed} from '@angular/core/testing';
import {HttpClientModule} from "@angular/common/http";

import {CertGenerationService} from './cert-generation.service';

describe('CertGenerationService', () => {
    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [
                HttpClientModule,
            ],
            providers: [CertGenerationService]
        });
    });

    it('should be created', inject([CertGenerationService], (service: CertGenerationService) => {
        expect(service).toBeTruthy();
    }));
});
