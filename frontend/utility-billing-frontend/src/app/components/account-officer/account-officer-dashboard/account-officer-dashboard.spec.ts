import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AccountOfficerDashboard } from './account-officer-dashboard';

describe('AccountOfficerDashboard', () => {
  let component: AccountOfficerDashboard;
  let fixture: ComponentFixture<AccountOfficerDashboard>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AccountOfficerDashboard]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AccountOfficerDashboard);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
