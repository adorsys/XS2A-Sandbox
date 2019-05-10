import {inject, TestBed} from '@angular/core/testing';
import {HttpClientModule} from '@angular/common/http';
import {JwtHelperService} from '@auth0/angular-jwt';
import {HttpClientTestingModule, HttpTestingController} from '@angular/common/http/testing';

import {AuthService} from './auth.service';
import {environment} from "../../environments/environment";
import {User} from "../models/user.model";

describe('AuthService', () => {
    let httpTestingController: HttpTestingController;
    let authService: AuthService;
    const url = `${environment.staffAccessResourceEndPoint + '/users'}`;


    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [
                HttpClientModule,
                HttpClientTestingModule,
            ],
            providers: [AuthService, JwtHelperService]
        });

        httpTestingController = TestBed.get(HttpTestingController);
        authService = TestBed.get(AuthService);
    });

    afterEach(() => {
        httpTestingController.verify();
    });

    it('should be created', inject([AuthService], (service: AuthService) => {
        expect(service).toBeTruthy();
    }));

    it('should delete token on logout', () => {
        authService.logout();
        expect(localStorage.getItem('token')).toBeNull();
    });

    it('should call authorize when login', () => {
        let getAuthorizationTokenSpy = spyOn(authService, 'authorize').and.callThrough();
        let credentialsMock = {login: 's', pin: 'q', role: 'STAFF'};
        authService.login(credentialsMock);
        expect(getAuthorizationTokenSpy).toHaveBeenCalled();
    });

    it('should test login method', () => {

        // isLoggedin() is false by default
        expect(authService.isLoggedIn()).toBeFalsy();

        // login credential is not correct
        let credentialsMock = {login: 'q', pin: 'q', role: 'STAFF'};
        authService.login(credentialsMock).subscribe(response => {
            expect(response).toBeFalsy()
        });

        let req = httpTestingController.expectOne(url + '/login');
        expect(req.cancelled).toBeFalsy();
        expect(req.request.method).toEqual('POST');
        req.flush(credentialsMock);
    });

    it('should test register method', () => {

        // login credential is not correct
        let credentialsMock = {
            email: 'test@test.de',
            login: 'test',
            branch: '12345678',
            pin: '123456'
        };

        authService.register(credentialsMock as User, credentialsMock.branch).subscribe(response => {
            expect(response.email).toBe('test@test.de')
        });

        let req = httpTestingController.expectOne(url + '/register?branch=12345678');
        expect(req.cancelled).toBeFalsy();
        expect(req.request.method).toEqual('POST');
        req.flush(credentialsMock);
    });

    it('should return expected list of users (HttpClient called once)', () => {
        const mockUsers = [
            {
                accountAccesses: [
                    {id: 'bNrPhmm3SC0vwm2Tf4KknM', iban: 'DE51250400903312345678', accessType: 'OWNER'},
                    {id: 'lcyeJaTxQrIhtuNQl-kF4E', iban: 'ME66929958485327905358', accessType: 'OWNER'}
                ],
                branch: 'fdf',
                email: 'foo@foo.de',
                id: 'J4tdJUEPQhglZAFgvo9aJc',
                login: 'test',
                pin: '$2a$10$hi7Cd4j9gd/ZBw7w.kbNVOzDNUgIEXUtG5ZJYvjjTGLjUwOR0qibu',
                scaUserData: [{id: 'HeJDea8LQE8rdLiJ6eKfhY', scaMethod: 'EMAIL', methodValue: 'foo@fool.de'}],
                userRoles: ['CUSTOMER']
            }
        ];

        /* userService.listUsers().subscribe(user => {
             expect(user[0].login).toEqual('test');
             expect(user[0].email).toEqual('foo@foo.de');
         });

         const req = httpTestingController.expectOne(url);
         expect(req.cancelled).toBeFalsy();
         expect(req.request.responseType).toEqual('json');
         expect(req.request.method).toEqual('GET');

         req.flush(mockUsers);*/
    })
});
