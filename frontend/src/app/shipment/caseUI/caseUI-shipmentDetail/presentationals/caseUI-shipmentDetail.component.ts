import {Component, Input} from "@angular/core";
import {ShipmentResource} from "../../../shipment-common/api/resources/shipment.resource";

@Component({
  selector: "educama-caseui-shipment-detail",
  templateUrl: "./caseUI-shipmentDetail.component.html",
  styleUrls: ["../caseUI-shipmentDetail-style.scss"]
})
export class CaseUIShipmentDetailComponent {
  @Input()
  public shipment: ShipmentResource;

  constructor() {
  }
}
