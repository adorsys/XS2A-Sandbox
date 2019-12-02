import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { TppService } from '../../services/tpp.service';

@Component({
    selector: 'app-settings',
    templateUrl: './settings.component.html',
    styleUrls: ['./settings.component.scss']
})
export class SettingsComponent {
    constructor(private tppService: TppService,
                private router: Router,
                private modalService: NgbModal) {}

    deleteTpp() {
        this.tppService.deleteTpp().subscribe(() => {
            localStorage.removeItem('access_token');
            this.router.navigate(['/login']);
        });
    }

    openDeleteConfirmation(content) {
        this.modalService.open(content).result.then(() => {
            this.deleteTpp();
        }, () => {});
    }
}
