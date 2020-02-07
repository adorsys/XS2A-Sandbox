import { HttpClientTestingModule } from '@angular/common/http/testing';
import { async, TestBed } from '@angular/core/testing';
import { from } from 'rxjs';

import { ConsentAuthorizeResponse } from '../../api/models/consent-authorize-response';
import { AisService } from './ais.service';

import LoginUsingPOSTParams = PSUAISProvidesAccessToOnlineBankingAccountFunctionalityService.LoginUsingPOSTParams;
import {PSUAISProvidesAccessToOnlineBankingAccountFunctionalityService} from "../../api/services/psuaisprovides-access-to-online-banking-account-functionality.service";
beforeEach(async(() => {
    TestBed.configureTestingModule({
        imports: [
            HttpClientTestingModule
        ],
        providers: []
    });
}));

describe('AisService', () => {
    let _aisService: AisService;
    let _params: LoginUsingPOSTParams;
    let _authResponse: ConsentAuthorizeResponse;
    beforeEach(() => {
        _aisService = TestBed.get(AisService);
        _params = {
            pin: '12345',
            login: 'anton.brueckner',
            encryptedConsentId: '45678uztredf899',
            authorisationId: '0987654wertzuio',

        };
        _authResponse = {
            authMessageTemplate: 'etwas',
            authorisationId: '45678uztredf899',
            scaStatus: 'received',
            scaMethods: [
                {
                    id: '1903', methodValue: 'SMS sca method',
                    scaMethod: 'EMAIL'
                }
            ]
        };
    });

    it('should check that the functions are defined', () => {
        expect(_aisService).toBeTruthy();
        expect(_aisService.aisAuthorise).not.toBeNull();
    });

    it('Should check that the method "authorise" is called', () => {
        let result: ConsentAuthorizeResponse = null;

        spyOn(_aisService, 'aisAuthorise').and.returnValue(from([_authResponse]));

        _aisService.aisAuthorise(_params).subscribe(response => result = response);

        expect(result).not.toBeNull();
        expect(result.scaStatus).toEqual(_authResponse.scaStatus);
        expect(result.scaMethods).toEqual(_authResponse.scaMethods);
        expect(_aisService.aisAuthorise).toHaveBeenCalledTimes(1);
    });
});
