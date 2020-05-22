import {HttpClientModule} from '@angular/common/http';
import {TestBed, inject} from '@angular/core/testing';
import {HttpClientTestingModule, HttpTestingController} from '@angular/common/http/testing';
import {TppUserService} from './tpp.user.service';
import {environment} from '../../environments/environment';
import { User } from '../models/user.model';

describe('TppUserService', () => {
    let httpMock: HttpTestingController;
    let tppUserService: TppUserService;
    const url = `${environment.tppBackend}`;

    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [HttpClientModule,
                HttpClientTestingModule],
            providers: [TppUserService]
        });
        tppUserService = TestBed.get(TppUserService);
        httpMock = TestBed.get(HttpTestingController);
    })

    it('should be created', () => {
    const service: TppUserService = TestBed.get(TppUserService);
    expect(service).toBeTruthy();
  });

    it('should load User info', () => {
        tppUserService.loadUserInfo();
    });

    it('should load user info', () => {
        let mockUser: User = {
            id: '12345',
            email: 'tes@adorsys.de',
            login: 'bob',
            branch: '34256',
            branchLogin: 'branchLogin',
            pin: '12345',
            scaUserData: [],
            accountAccesses: [{
                accessType: 'OWNER',
                iban: 'FR87760700254556545403'
            }]
        } as User;
        tppUserService.getUserInfo().subscribe((data: User) => {
            expect(data.pin).toBe('12345');
        });
        const req = httpMock.expectOne(url + '/users/me');
        expect(req.request.method).toBe('GET');
        req.flush({pin:'12345'});
        httpMock.verify();
    });

    it('should update User Info', () => {
        let mockUser: User = {
                id: 'XXXXXX',
                email: 'tes@adorsys.de',
                login: 'bob',
                branchLogin: 'branchLogin',
                branch: '34256',
                pin: '12345',
                scaUserData: [],
                accountAccesses: [{
                    accessType: 'OWNER',
                    iban: 'FR87760700254556545403'
                }]
            } as User;
        tppUserService.updateUserInfo(mockUser).subscribe((data: User) => {
            expect(data.login).toBe('bob');
        });
        const req = httpMock.expectOne(url + '/users');
        expect(req.request.method).toBe('PUT');
        req.flush({login: 'bob'});
        httpMock.verify();
    });
});
