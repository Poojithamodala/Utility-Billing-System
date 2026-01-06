import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ManageTariff } from './manage-tariff';

describe('ManageTariff', () => {
  let component: ManageTariff;
  let fixture: ComponentFixture<ManageTariff>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ManageTariff]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ManageTariff);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
