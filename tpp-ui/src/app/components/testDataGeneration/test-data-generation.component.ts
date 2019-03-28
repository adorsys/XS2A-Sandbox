import {Component, OnInit} from '@angular/core';
import {FormGroup} from "@angular/forms";
import {TestDataGenerationService} from "../../services/test.data.generation.service"
import {DomSanitizer, SafeResourceUrl} from "@angular/platform-browser";

@Component({
    selector: 'test-data-generation',
    templateUrl: './test-data-generation.component.html',
    styleUrls: ['./test-data-generation.component.css']
})
export class TestDataGenerationComponent implements OnInit {
    private form = new FormGroup({});
    private fileUrl: SafeResourceUrl;

    constructor(private generationService: TestDataGenerationService,
                private sanitizer: DomSanitizer) {
        this.generationService = generationService;
    }

    submitted: boolean;

    ngOnInit(): void {
    }

    generate() {
        return this.generationService.generateTestData()
            .subscribe(data => {
                const blob = new Blob([data], {type: 'plain/text'});
                let link = document.createElement("a");
                link.setAttribute("href", window.URL.createObjectURL(blob));
                link.setAttribute("download", "NISP-Test-Data.yml");
                document.body.appendChild(link);
                link.click();
            });
    }
}
