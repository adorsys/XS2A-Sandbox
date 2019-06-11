import {Component, OnInit} from '@angular/core';
import {FormGroup} from "@angular/forms";
import {TestDataGenerationService} from "../../services/test.data.generation.service"
import {DomSanitizer, SafeResourceUrl} from "@angular/platform-browser";
import {InfoService} from "../../commons/info/info.service";

@Component({
    selector: 'test-data-generation',
    templateUrl: './test-data-generation.component.html',
    styleUrls: ['./test-data-generation.component.scss']
})
export class TestDataGenerationComponent implements OnInit {
    submitted: boolean;
    generatePaymentsFlag: boolean;
    private form = new FormGroup({});
    private fileUrl: SafeResourceUrl;
    private message = 'Test data has been successfully generated. The automatic download of the test yml file will start within some seconds.';

    constructor(private generationService: TestDataGenerationService,
                private infoService: InfoService,
                private sanitizer: DomSanitizer) {
        this.generationService = generationService;
    }

    ngOnInit(): void {
    }

    generate() {
        return this.generationService.generateTestData(this.generatePaymentsFlag)
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

                });
    }
}
