import {inject, TestBed} from '@angular/core/testing';
import {HttpClientModule} from '@angular/common/http';
import {HttpClientTestingModule, HttpTestingController} from '@angular/common/http/testing';

import {UserService} from './user.service';
import {environment} from "../../environments/environment";

describe('UserService', () => {
    let httpTestingController: HttpTestingController;
    let userService: UserService;

    let roles = {param: 'roles', value: 'CUSTOMER'};
    const url = `${environment.staffAccessResourceEndPoint + '/users'}?${roles.param}=${roles.value}`;

    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [
                HttpClientModule,
                HttpClientTestingModule,
            ],
            providers: [UserService]
        });

        httpTestingController = TestBed.get(HttpTestingController);
        userService = TestBed.get(UserService);
    });

    afterEach(() => {
        httpTestingController.verify();
    });

    it('should be created', inject([UserService], (service: UserService) => {
        expect(service).toBeTruthy();
    }));

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

        userService.listUsers().subscribe(user => {
            expect(user[0].login).toEqual('test');
            expect(user[0].email).toEqual('foo@foo.de');
        });

        const req = httpTestingController.expectOne(url);
        expect(req.cancelled).toBeFalsy();
        expect(req.request.responseType).toEqual('json');
        expect(req.request.method).toEqual('GET');

        req.flush(mockUsers);
    })
});

