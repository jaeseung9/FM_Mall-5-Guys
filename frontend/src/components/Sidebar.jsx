import React, { useState, useEffect } from 'react';
import { categoryAPI } from '../services/api';

const Sidebar = ({ onFilterChange }) => {
    const [categories, setCategories] = useState([]);
    const [selectedCategory, setSelectedCategory] = useState(null);

    // ✅ 브랜드는 "이름 문자열"이 아니라 "ID 배열"로 관리
    const [selectedBrandIds, setSelectedBrandIds] = useState([]);
    const [priceRange, setPriceRange] = useState({ min: '', max: '' });

    // ✅ 실제 DB brand_id 값에 맞게 수정해야 함
    const brands = [
        { id: 1, label: '삼성' },
        { id: 2, label: 'LG' },
        { id: 3, label: '다이슨' },
        { id: 4, label: '기타' },
    ];

    useEffect(() => {
        loadCategories();
    }, []);

    const loadCategories = async () => {
        try {
            const response = await categoryAPI.getAllCategories();
            console.log('카테고리 데이터:', response.data);
            setCategories(response.data || []);
        } catch (error) {
            console.error('카테고리 로딩 실패:', error);
            setCategories([]);
        }
    };

    const applyFilters = (filtersOverride = null) => {
        const filterData = filtersOverride || {
            categoryId: selectedCategory,
            brandIds: selectedBrandIds,   // ✅ brandIds로 통일
            priceRange
        };

        if (onFilterChange) {
            onFilterChange(filterData);
        }
    };

    const handleCategoryClick = (categoryId) => {
        setSelectedCategory(categoryId);

        applyFilters({
            categoryId,
            brandIds: selectedBrandIds,
            priceRange
        });
    };

    const handleBrandChange = (brandId) => {
        const updated = selectedBrandIds.includes(brandId)
            ? selectedBrandIds.filter(id => id !== brandId)
            : [...selectedBrandIds, brandId];

        setSelectedBrandIds(updated);

        // ✅ 브랜드 선택 바뀔 때마다 바로 필터 적용
        applyFilters({
            categoryId: selectedCategory,
            brandIds: updated,
            priceRange
        });
    };

    return (
        <aside className="sidebar">
            {/* 카테고리 섹션 */}
            <section className="sidebar__section">
                <h2 className="sidebar__title">카테고리</h2>
                <ul className="sidebar__list">
                    <li key="all">
                        <a
                            href="#"
                            onClick={(e) => {
                                e.preventDefault();
                                handleCategoryClick(null);
                            }}
                            style={{
                                fontWeight: selectedCategory === null ? 'bold' : 'normal',
                                color: selectedCategory === null ? '#111827' : '#4b5563',
                            }}
                        >
                            전체
                        </a>
                    </li>
                    {categories.map((category) => (
                        <li key={category.categoryId}>
                            <a
                                href="#"
                                onClick={(e) => {
                                    e.preventDefault();
                                    handleCategoryClick(category.categoryId);
                                }}
                                style={{
                                    fontWeight:
                                        selectedCategory === category.categoryId ? 'bold' : 'normal',
                                    color:
                                        selectedCategory === category.categoryId
                                            ? '#111827'
                                            : '#4b5563',
                                }}
                            >
                                {category.categoryName}
                            </a>
                        </li>
                    ))}
                </ul>
            </section>

            {/* 브랜드 섹션 */}
            <section className="sidebar__section">
                <h2 className="sidebar__title">브랜드</h2>
                {brands.map((brand) => (
                    <label key={brand.id} className="sidebar__checkbox">
                        <input
                            type="checkbox"
                            checked={selectedBrandIds.includes(brand.id)}
                            onChange={() => handleBrandChange(brand.id)}
                        />
                        {brand.label}
                    </label>
                ))}
            </section>

            {/* 가격대 섹션 */}
            <section className="sidebar__section">
                <h2 className="sidebar__title">가격대</h2>
                <div className="price-range">
                    <input
                        type="number"
                        placeholder="최소"
                        value={priceRange.min}
                        onChange={(e) =>
                            setPriceRange({ ...priceRange, min: e.target.value })
                        }
                    />
                    <span>~</span>
                    <input
                        type="number"
                        placeholder="최대"
                        value={priceRange.max}
                        onChange={(e) =>
                            setPriceRange({ ...priceRange, max: e.target.value })
                        }
                    />
                </div>
                <button
                    className="btn btn--outline full-width"
                    onClick={() => applyFilters()}
                >
                    필터 적용
                </button>
            </section>
        </aside>
    );
};

export default Sidebar;
