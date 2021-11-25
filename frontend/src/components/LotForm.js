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
                            Picture
                        </Form.Label>
                        <Form.Control required defaultValue={props.lot.picture} onChange={props.handleChange("picture")}/>
                        <Form.Control.Feedback type="invalid">
                            The value cannot be empty
                        </Form.Control.Feedback>
                    </Form.Group>
                    <Form.Group>
                        <Form.Label>
                            Start bank
                        </Form.Label>
                        <Form.Control required type="number" min={1} defaultValue={props.lot.minBank} onChange={props.handleChange("minBank")}/>
                        <Form.Control.Feedback type="invalid">
                            The min value is 1
                        </Form.Control.Feedback>
                    </Form.Group>
                    <Form.Group>
                        <Form.Label>
                            Minimal bet
                        </Form.Label>
                        <Form.Control required type="number" min={1} defaultValue={props.lot.step} onChange={props.handleChange("step")}/>
                        <Form.Control.Feedback type="invalid">
                            The min value is 1
                        </Form.Control.Feedback>
                    </Form.Group>
                </div>
            </div>

    )
}

export default LotForm;