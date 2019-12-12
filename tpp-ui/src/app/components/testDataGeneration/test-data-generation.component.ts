import { Component, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { Router } from '@angular/router';

import { InfoService } from '../../commons/info/info.service';
import { TestDataGenerationService } from '../../services/test.data.generation.service';
import {CurrencyService} from "../../services/currency.service";

@Component({
    selector: 'test-data-generation',
    templateUrl: './test-data-generation.component.html',
    styleUrls: ['./test-data-generation.component.scss']
})
export class TestDataGenerationComponent implements OnInit {
    submitted: boolean;
    generatePaymentsFlag: boolean;
    private message = 'Test data has been successfully generated. The automatic download of the test yml file will start within some seconds.';

    selectedCurrency;
    currencyList: Array<string> = [];

    constructor(private generationService: TestDataGenerationService,
                private infoService: InfoService,
                private router: Router,
                private sanitizer: DomSanitizer,
                private currencyService: CurrencyService) {
        this.generationService = generationService;
    }

    ngOnInit(): void {
      this.currencyList = this.currencyService.currencyList;
    }

    generate() {
        return this.generationService.generateTestData(this.selectedCurrency, this.generatePaymentsFlag)
            .subscribe(data => {
                    this.infoService.openFeedback(this.message);

                    setTimeout(() => {
                        const blob = new Blob([data], {type: 'plain/text'});
                        let link = document.createElement("a");
                        link.setAttribute("href", window.URL.createObjectURL(blob));
                        link.setAttribute("download", "NISP-Test-Data.yml");
                        document.body.appendChild(link);
                        link.click();
                    }, 3000);
                this.router.navigate(['/accounts']);
                });
    }
}
