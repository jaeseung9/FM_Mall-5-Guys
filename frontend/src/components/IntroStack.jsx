import React, { useEffect, useRef, useState } from 'react';
import './IntroStack.css';

const IntroStack = ({ onComplete }) => {
    const [visibleCards, setVisibleCards] = useState([false, false, false, false]);
    const observerRef = useRef(null);
    const cardTriggersRef = useRef([]);

    useEffect(() => {
        // Intersection Observer 설정
        observerRef.current = new IntersectionObserver(
            (entries) => {
                entries.forEach((entry) => {
                    if (entry.isIntersecting) {
                        const index = parseInt(entry.target.dataset.index);
                        setVisibleCards(prev => {
                            const newState = [...prev];
                            newState[index] = true;
                            return newState;
                        });
                    }
                });
            },
            {
                threshold: 0.5, // 50% 보이면 트리거
                rootMargin: '-100px 0px'
            }
        );

        // 각 트리거에 observer 연결
        cardTriggersRef.current.forEach((trigger) => {
            if (trigger) {
                observerRef.current.observe(trigger);
            }
        });

        return () => {
            if (observerRef.current) {
                observerRef.current.disconnect();
            }
        };
    }, []);

    return (
        <div className="intro-stack-wrapper">
            <div className="intro-stack-scroll-container">
                {/* 카드 고정 영역 */}
                <div className="intro-stack-fixed-area">
                    {/* 카드 1 */}
                    <div
                        className={`intro-card ${visibleCards[0] ? 'is-stacked' : ''}`}
                        style={{ '--stack-index': 0 }}
                    >
                        <div className="card-content card-purple">
                            <h2 className="card-title">FM 전자몰</h2>
                            <p className="card-text">프리미엄 가전제품의 모든 것</p>
                        </div>
                    </div>

                    {/* 카드 2 */}
                    <div
                        className={`intro-card ${visibleCards[1] ? 'is-stacked' : ''}`}
                        style={{ '--stack-index': 1 }}
                    >
                        <div className="card-content card-pink">
                            <div className="card-icon">🎁</div>
                            <h2 className="card-title">오늘의 특가</h2>
                            <p className="card-text">최대 50% 할인 이벤트 진행중</p>
                        </div>
                    </div>

                    {/* 카드 3 */}
                    <div
                        className={`intro-card ${visibleCards[2] ? 'is-stacked' : ''}`}
                        style={{ '--stack-index': 2 }}
                    >
                        <div className="card-content card-blue">
                            <div className="card-icon">🚚</div>
                            <h2 className="card-title">무료 배송</h2>
                            <p className="card-text">전 품목 무료 배송 서비스</p>
                        </div>
                    </div>

                    {/* 카드 4 */}
                    <div
                        className={`intro-card ${visibleCards[3] ? 'is-stacked' : ''}`}
                        style={{ '--stack-index': 3 }}
                    >
                        <div className="card-content card-orange">
                            <div className="card-icon">⚡</div>
                            <h2 className="card-title">빠른 배송</h2>
                            <p className="card-text">오늘 주문, 내일 도착</p>
                            <button className="start-button" onClick={onComplete}>
                                쇼핑 시작하기 →
                            </button>
                        </div>
                    </div>
                </div>

                {/* 스크롤 트리거 영역 (투명) */}
                <div className="intro-stack-triggers">
                    <div
                        className="trigger-zone"
                        data-index="0"
                        ref={el => cardTriggersRef.current[0] = el}
                    />
                    <div
                        className="trigger-zone"
                        data-index="1"
                        ref={el => cardTriggersRef.current[1] = el}
                    />
                    <div
                        className="trigger-zone"
                        data-index="2"
                        ref={el => cardTriggersRef.current[2] = el}
                    />
                    <div
                        className="trigger-zone"
                        data-index="3"
                        ref={el => cardTriggersRef.current[3] = el}
                    />
                </div>
            </div>

            <button className="skip-button" onClick={onComplete}>
                건너뛰기
            </button>
        </div>
    );
};

export default IntroStack;