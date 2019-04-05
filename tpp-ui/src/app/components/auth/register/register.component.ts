import {Component, OnInit} from '@angular/core';
import {FormArray, FormControl, FormGroup, Validators} from "@angular/forms";
import {AuthService} from "../../../services/auth.service";
import {Router} from "@angular/router";
import {CertGenerationService} from "../../../services/cert-generation.service";
import {combineLatest} from "rxjs";
import JSZip from 'jszip';
import {InfoService} from "../../../commons/info/info.service";

@Component({
    selector: 'app-register',
    templateUrl: './register.component.html',
    styleUrls: ['./register.component.css']
})
export class RegisterComponent implements OnInit {


    public userForm: FormGroup;
    public certificateForm: FormGroup;

    public rolesOptions = ['PIISP', 'PISP', 'AISP'];

    public generateCertificate: boolean;
    public submitted: boolean;
    public errorMessage: string; //TODO: errors handling with error interceptor

    public static generateZipFile(certBlob, keyBlob): Promise<any> {
        const zip = new JSZip();
        zip.file('certificate.pem', certBlob);
        zip.file('private.key', keyBlob);
        return zip.generateAsync({ type: 'blob' });
    }

    constructor(private service: AuthService,
                private certGenerationService: CertGenerationService,
                private infoService: InfoService,
                private router: Router) {
    }

    ngOnInit() {
        this.initializeRegisterForm();
        this.initializeCertificateGeneratorForm();
    }

    public onSubmit(branch: HTMLInputElement): void {
        this.submitted = true;

        if (this.userForm.invalid) {
            return;
        }

        if (this.generateCertificate) {
            if (this.certificateForm.invalid) {
                return;
            }

            // combine observables
            combineLatest([
                this.service.register(this.userForm.value, branch.value),
                this.certGenerationService.generate(this.certificateForm.value)
            ]).subscribe((combinedData: any) => {

                // get cert generation params
                const encodedCert = combinedData[1].encodedCert;
                const privateKey = combinedData[1].privateKey;

                this.createZipUrl(encodedCert, privateKey).then(url =>
                    this.router.navigate(['/login'])
                        .then(() => {

                            this.infoService.openFeedback(`You have been successfully registered and your certificate generated.
                            The download will start automatically within the 2 seconds`);

                            setTimeout(() => {
                                this.downloadFile(url);
                            }, 2000, url)

                        })
                );
            });
        } else {
            this.service.register(this.userForm.value, branch.value)
                .subscribe(() => {
                    this.router.navigate(['/login'])
                        .then(() => {
                            this.infoService.openFeedback(`You have been successfully registered.`);
                        });
                    }, () => {
                        this.infoService.openFeedback('TPP with this login or email exists already', {
                            severity: 'error'
                        })
                });

        }
    }

    public handleTppRoles(roleInput: HTMLInputElement): void {
        const roleInputValue = roleInput.value;

        const roleIndex = this.roles.value.findIndex(role => roleInputValue === role);

        if (roleIndex == -1) {
            this.roles.push(new FormControl(roleInputValue));
        } else {
            this.roles.removeAt(roleIndex);
        }
    }

    private initializeRegisterForm(): void {
        this.userForm = new FormGroup({
            branch: new FormControl('', Validators.required),
            login: new FormControl('', Validators.required),
            email: new FormControl('', Validators.required),
            pin: new FormControl('', Validators.required)
        });
    }

    private initializeCertificateGeneratorForm(): void {
        this.certificateForm = new FormGroup({
            authorizationNumber: new FormControl('ID12345', Validators.required),
            organizationName: new FormControl('Awesome TPP', Validators.required),
            countryName: new FormControl('Germany'),
            domainComponent: new FormControl('awesome-tpp.de', Validators.required),
            localityName: new FormControl('Nuremberg'),
            organizationUnit: new FormControl('IT department'),
            stateOrProvinceName: new FormControl('Bayern'),
            validity: new FormControl('365', [
                Validators.required,
                Validators.pattern("^[0-9]*$"),
                Validators.min(0)
            ]),
            roles: new FormArray([], Validators.required)
        });
    }

    private createZipUrl(encodedCert: string, privateKey: string): Promise<string> {
        const blobCert = new Blob([encodedCert], {
            type: 'text/plain',
        });
        const blobKey = new Blob([privateKey], {
            type: 'text/plain',
        });
        return RegisterComponent.generateZipFile(blobCert, blobKey).then(
            zip => {
                return window.URL.createObjectURL(zip);
            }
        );
    }

    private downloadFile(url: string) {
        const element = document.createElement('a');
        element.setAttribute('href', url);
        element.setAttribute('download', 'psu_cert.zip');
        element.style.display = 'none';
        document.body.appendChild(element);
        element.click();
        document.body.removeChild(element);
    }

    // user form controls
    get login() {
        return this.userForm.get('login');
    }

    get branch() {
        return this.userForm.get('branch');
    }

    get email() {
        return this.userForm.get('email');
    }

    get pin() {
        return this.userForm.get('pin');
    }

    // certificate generation form controls
    get authorizationNumber(): FormControl {
        return <FormControl>this.certificateForm.get('authorizationNumber');
    }

    get organizationName(): FormControl {
        return <FormControl>this.certificateForm.get('organizationName');
    }

    get domainComponent(): FormControl {
        return <FormControl>this.certificateForm.get('domainComponent');
    }

    get validity(): FormControl {
        return <FormControl>this.certificateForm.get('validity');
    }

    get roles(): FormArray {
        return <FormArray>this.certificateForm.get('roles');
    }
}
