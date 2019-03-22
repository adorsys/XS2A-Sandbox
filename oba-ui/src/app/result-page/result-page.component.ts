import { Component, OnInit } from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';

@Component({
    selector: 'app-result-page',
    templateUrl: './result-page.component.html',
    styleUrls: ['./result-page.component.scss']
})
export class ResultPageComponent implements OnInit {

    show: boolean;

    constructor(public router: Router,
                private route: ActivatedRoute,) {
        this.route.params.subscribe(response => {
            this.show = (response.id == 1);
        });
    }

    public ngOnInit(): void {
    }

    public nextPage(): void {
        this.router.navigate(['tan-confirm']); // please insert the TPP ok url on this line
    }
    public decline(): void {
        this.router.navigate(['login']); // please insert the TPP not ok  url on this line
    }

}
