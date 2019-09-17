import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import JSZip from 'jszip';
import { combineLatest } from 'rxjs';

import { InfoService } from '../../../commons/info/info.service';
import { AuthService } from '../../../services/auth.service';
import { CertGenerationService } from '../../../services/cert-generation.service';
import { CustomizeService } from '../../../services/customize.service';

@Component({
    selector: 'app-register',
    templateUrl: './register.component.html',
    styleUrls: ['../auth.component.scss']
})
export class RegisterComponent implements OnInit {


    public userForm: FormGroup;
    public certificateValue = {};

    public generateCertificate: boolean;
    public submitted: boolean;
    public errorMessage: string;

    constructor(private service: AuthService,
                private certGenerationService: CertGenerationService,
                private infoService: InfoService,
                private router: Router,
                private formBuilder: FormBuilder,
                public customizeService: CustomizeService) {
    }

    ngOnInit() {
        this.initializeRegisterForm();
    }

    getCertificateValue(event) {
        this.certificateValue = event;
    }

    public onSubmit(): void {

        if (this.userForm.invalid || !this.certificateValue) {
            this.submitted = true;
            return;
        }

        let message: string;

        if (this.generateCertificate && this.certificateValue) {
            // combine observables
            combineLatest([
                this.service.register(this.userForm.value),
                this.certGenerationService.generate(this.certificateValue)
            ]).subscribe((combinedData: any) => {

                // get cert generation params
                const encodedCert = combinedData[1].encodedCert;
                const privateKey = combinedData[1].privateKey;

                this.createZipUrl(encodedCert, privateKey).then(url => {
                        message = 'You have been successfully registered and your certificate generated. The download will start automatically within the 2 seconds';
                        this.navigateAndGiveFeedback(url, message);
                    }
                );
            });
        } else {
            this.service.register(this.userForm.value)
                .subscribe(() => {
                    message = 'You have been successfully registered.';
                    this.navigateAndGiveFeedback('', message);
                });
        }
    }

    private navigateAndGiveFeedback(url: string, message: string) {
        this.router.navigate(['/login'])
            .then(() => {
                this.infoService.openFeedback(message);
                if (url) {
                    setTimeout(() => {
                        this.downloadFile(url);
                    }, 2000, url)
                }
            })
    }

    private generateZipFile(certBlob, keyBlob): Promise<any> {
        const zip = new JSZip();
        zip.file('certificate.pem', certBlob);
        zip.file('private.key', keyBlob);
        return zip.generateAsync({type: 'blob'});
    }

    private initializeRegisterForm(): void {
        this.userForm = this.formBuilder.group({
            id: ['', [
                Validators.required,
                Validators.pattern("^[0-9]*$"),
                Validators.minLength(8),
                Validators.maxLength(8)
            ]],
            login: ['', Validators.required],
            email: ['', [Validators.required, Validators.pattern(new RegExp(/^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/)),]],
            pin: ['', Validators.required]
        });
    }

    private createZipUrl(encodedCert: string, privateKey: string): Promise<string> {
        const blobCert = new Blob([encodedCert], {
            type: 'text/plain',
        });
        const blobKey = new Blob([privateKey], {
            type: 'text/plain',
        });
        return this.generateZipFile(blobCert, blobKey).then(
            zip => {
                return window.URL.createObjectURL(zip);
            }
        );
    }

    private downloadFile(url: string) {
        const element = document.createElement('a');
        element.setAttribute('href', url);
        element.setAttribute('download', 'tpp_cert.zip');
        element.style.display = 'none';
        document.body.appendChild(element);
        element.click();
        document.body.removeChild(element);
    }
}
