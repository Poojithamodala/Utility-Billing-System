import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AccountsPayments } from './accounts-payments';

describe('AccountsPayments', () => {
  let component: AccountsPayments;
  let fixture: ComponentFixture<AccountsPayments>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AccountsPayments]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AccountsPayments);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
