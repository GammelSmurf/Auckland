import {Form} from "react-bootstrap";
import React from "react";

const LotForm = (props) => {
    return (
            <div style={{overflow: "hidden", minHeight: "400px", padding: "3px"}}>
                <div className="basicAnim">
                    <Form.Group>
                        <Form.Label>
                            Name
                        </Form.Label>
                        <Form.Control required defaultValue={props.lot.name} onChange={props.handleChange("name")}/>
                        <Form.Control.Feedback type="invalid">
                            The value cannot be empty
                        </Form.Control.Feedback>
                    </Form.Group>
                    <Form.Group>
                        <Form.Label>
                            Description
                        </Form.Label>
                        <Form.Control as="textarea" rows={4} required defaultValue={props.lot.description} onChange={props.handleChange("description")}/>
                        <Form.Control.Feedback type="invalid">
                            The value cannot be empty
                        </Form.Control.Feedback>
                    </Form.Group>
                    <Form.Group>
                        <Form.Label>
                            Picture link
                        </Form.Label>
                        <Form.Control defaultValue={props.lot.pictureLink} onChange={props.handleChange("pictureLink")}/>
                        <Form.Control.Feedback type="invalid">
                            The value cannot be empty
                        </Form.Control.Feedback>
                    </Form.Group>
                    <Form.Group>
                        <Form.Label>
                            Minimal price
                        </Form.Label>
                        <Form.Control required type="number" min={1} defaultValue={props.lot.minPrice} onChange={props.handleChange("minPrice")}/>
                        <Form.Control.Feedback type="invalid">
                            The min value is 1
                        </Form.Control.Feedback>
                    </Form.Group>
                    <Form.Group>
                        <Form.Label>
                            Price increase minimal step
                        </Form.Label>
                        <Form.Control required type="number" min={1} defaultValue={props.lot.priceIncreaseMinStep} onChange={props.handleChange("priceIncreaseMinStep")}/>
                        <Form.Control.Feedback type="invalid">
                            The min value is 1
                        </Form.Control.Feedback>
                    </Form.Group>
                </div>
            </div>

    )
}

export default LotForm;