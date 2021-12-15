import {Carousel} from "react-bootstrap";
import React from "react";


const LotCarousel = (props) => {

    return(
            <Carousel>
                {props.lots.map(lot =>
                    <Carousel.Item key={lot.id}>
                        <div>
                            <img
                                className="d-block w-100"
                                src={lot.pictureLink ? lot.pictureLink : props.errorPictureURL}
                                height="300"
                                alt="No image"
                                style={{filter: "brightness(60%)", objectFit: "cover"}}
                                onError={(e) => e.target.src = props.errorPictureURL}
                            />
                        </div>
                        {props.status === 'RUNNING' ?
                            <div>
                                <div style={{position: "absolute", top: 0, left: "15%", right: "15%"}}>
                                    <h3 className='text-white'>{lot.name}</h3>
                                </div>
                                <Carousel.Caption>
                                    <h3 className='text-white'>{'Opening price: ' + lot.minPrice}$</h3>
                                    <h3 className='text-white'>Minimal bid: {lot.priceIncreaseMinStep}$</h3>
                                    <h3 className='text-white'>Time raise: {props.parseTimeRaise(props.auction.extraTime)}</h3>
                                </Carousel.Caption>
                            </div>
                            :
                            <div>
                                <div style={{position: "absolute", top: 0, left: "15%", right: "15%"}}>
                                    {props.status === 'FINISHED' ?
                                        lot.winner ?
                                            <>
                                                <h3 className='text-white'>Winner: <span>{lot.winner.username}</span>
                                                </h3>
                                                <h3 className='text-white'>for {lot.winPrice}$</h3>
                                            </>
                                            :
                                            <h3 className='text-white'>Winner: nobody</h3>
                                        :
                                        <h3 className='text-white'>{'Opening price: ' + lot.minPrice} $</h3>}
                                    {props.status === 'RUNNING' &&
                                    <>
                                        <h3 className='text-white'>Minimal bid: {lot.priceIncreaseMinStep}</h3>
                                        <h3 className='text-white'>Time raise: {props.auction.extraTime}</h3>
                                    </>
                                    }
                                </div>
                                <Carousel.Caption>
                                    <h3>{lot.name}</h3>
                                    <p style={{
                                        overflow: 'hidden',
                                        textOverflow: 'ellipsis',
                                        display: '-webkit-box',
                                        WebkitLineClamp: 3,
                                        WebkitBoxOrient: 'vertical'
                                    }}>{lot.description}</p>
                                </Carousel.Caption>
                            </div>
                        }

                    </Carousel.Item>
                )}

            </Carousel>
    )
}

export default LotCarousel;