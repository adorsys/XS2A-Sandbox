import {Component, OnInit, ViewChild} from '@angular/core';
import {FormArray, FormBuilder, FormGroup, Validators} from '@angular/forms';
import {ActivatedRoute, Router} from '@angular/router';
import {UserService} from '../../../services/user.service';
import {User} from '../../../models/user.model';
import {InfoService} from '../../../commons/info/info.service';
import {ScaMethods} from '../../../models/scaMethods';
import {TppManagementService} from '../../../services/tpp-management.service';
import {NgbTypeahead} from '@ng-bootstrap/ng-bootstrap';
import {merge, Observable, Subject} from 'rxjs';
import {debounceTime, distinctUntilChanged, filter, map} from 'rxjs/operators';
import {ADMIN_KEY} from '../../../commons/constant/constant';

@Component({
  selector: 'app-user-create',
  templateUrl: './user-create.component.html',
  styleUrls: ['./user-create.component.scss'],
})
export class UserCreateComponent implements OnInit {
  @ViewChild('instance', {static: true}) instance: NgbTypeahead;

  focus$ = new Subject<User[]>();
  click$ = new Subject<User[]>();
  tppId: string;

  id: string;
  users: User[];
  admin: string;
  methods: string[];
  userForm: FormGroup;
  submitted: boolean;

  constructor(
    private userService: UserService,
    private formBuilder: FormBuilder,
    private router: Router,
    private infoService: InfoService,
    private route: ActivatedRoute,
    private tppManagementService: TppManagementService
  ) {
  }

  get formControl() {
    return this.userForm.controls;
  }

  ngOnInit() {
    this.admin = localStorage.getItem(ADMIN_KEY);
    this.listUsers();
    this.setupUserFormControl();
    this.getMethodsValues();
  }

  listUsers() {
    if (this.admin === 'true') {
      this.tppManagementService.getTpps(0, 500).subscribe((resp: any) => {
        this.users = resp.tpps;
      });
    }
  }

  search: (obs: Observable<string>) => Observable<User[]> = (text$: Observable<string>) => {
    const debouncedText$ = text$.pipe(debounceTime(200), distinctUntilChanged());
    const clicksWithClosedPopup$ = this.click$.pipe(filter(() =>  this.instance && !this.instance.isPopupOpen()));
    const inputFocus$ = this.focus$;
    return merge(debouncedText$, inputFocus$, clicksWithClosedPopup$)
      .pipe(
        map((searchText: string) => (searchText ? this.users : this.users.filter((user) => user))
        ));
  }

  setupUserFormControl(): void {
    this.userForm = this.formBuilder.group({
      scaUserData: this.formBuilder.array([this.initScaData()]),
      tppId: ['', [Validators.required]],
      email: ['', [Validators.required, Validators.email]],
      login: ['', Validators.required],
      pin: ['', [Validators.required, Validators.minLength(5)]],
      userRoles: this.formBuilder.array(['CUSTOMER'])
    });
  }

  public inputFormatterValue = (user: User) => {
    if (user) {
      return user.branch;
    }
    return user;
  }

  public resultFormatterValue = (user: User) => {
    if (user) {
      this.userForm.get('tppId').setValue(user.branch);
      return user.branch;
    }
    return user;
  }

  initScaData() {
    const emailValidators = [
      Validators.required,
      Validators.pattern(
        new RegExp(
          /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/
        )
      ),
    ];

    const scaData = this.formBuilder.group({
      scaMethod: ['', Validators.required],
      methodValue: [''],
      staticTan: [{value: '', disabled: true}],
      usesStaticTan: [false],
    });

    scaData
      .get('usesStaticTan')
      .valueChanges.subscribe((bool: boolean = true) => {
      if (bool) {
        scaData.get('staticTan').setValidators(Validators.required);
        scaData.get('methodValue').setValidators(Validators.required);
        scaData.get('staticTan').enable();
      } else {
        scaData.get('staticTan').clearValidators();
        scaData.get('staticTan').disable();
        scaData.get('staticTan').setValue('');
      }
      scaData.get('staticTan').updateValueAndValidity();
      scaData.get('methodValue').updateValueAndValidity();
    });

    scaData.get('staticTan').valueChanges.subscribe((value) => {
      if (value === ScaMethods.EMAIL) {
        scaData.get('staticTan').setValidators(emailValidators);
      } else if (value === ScaMethods.MOBILE) {
        scaData
          .get('staticTan')
          .setValidators([
            Validators.required,
            Validators.pattern(
              new RegExp(/^[+]*[(]{0,1}[0-9]{1,4}[)]{0,1}[-\s\./0-9]*$/)
            ),
          ]);
      } else {
        scaData.get('scaMethod').setValidators([Validators.required]);
      }
    });
    return scaData;
  }

  addScaDataItem() {
    const control = <FormArray>this.userForm.controls['scaUserData'];
    control.push(this.initScaData());
  }

  removeScaDataItem(i: number) {
    const control = <FormArray>this.userForm.controls['scaUserData'];
    control.removeAt(i);
  }

  onSubmit() {
    this.submitted = true;

    if (this.userForm.invalid) {
      console.log('validation', this.userForm.invalid)
      return;
    }

    if (this.admin === 'true') {
      this.tppManagementService
        .createUser(this.userForm.value, this.userForm.get('tppId').value)
        .subscribe(() => this.router.navigate(['/users/all']));
    } else if (this.admin === 'false') {
      this.userService
        .createUser(this.userForm.value)
        .subscribe(() => {this.router.navigateByUrl('/users/all');
        },
        () => {
          this.infoService.openFeedback(
            'Provided Login or Email are already taken',
            {
              severity: 'error',
            }
          );
        }
      );
    }
  }

  getMethodsValues() {
    this.methods = Object.keys(ScaMethods);
  }

  onCancel() {
    this.router.navigate(['/users/all']);
  }
}
