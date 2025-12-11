import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { productAPI, categoryAPI, adminProductAPI } from '../services/api';

const AdminProductPage = () => {
    const [products, setProducts] = useState([]);
    const [categories, setCategories] = useState([]);
    const [rowCategories, setRowCategories] = useState([]);
    const [brands, setBrands] = useState([]);
    const [loading, setLoading] = useState(true);
    const [showAddModal, setShowAddModal] = useState(false);
    const [showEditModal, setShowEditModal] = useState(false);
    const [editingProduct, setEditingProduct] = useState(null);
    const navigate = useNavigate();

    // 상품 폼 데이터 - 백엔드 DTO와 정확히 일치하도록 수정
    const [formData, setFormData] = useState({
        productName: '',
        productPrice: '',
        stockQuantity: '',
        categoryId: '',  // categoryCode -> categoryId로 변경
        rowCategoryId: '',  // rowCategoryCode -> rowCategoryId로 변경
        brandId: '',
        modelName: '',
        capacity: '',
        sizeInch: '',
        isInstallationRequired: 'N',
        productStatus: 'ACTIVE',
        description: ''
    });

    useEffect(() => {
        loadProducts();
        loadCategories();
        loadBrands();
    }, []);

    const loadProducts = async () => {
        try {
            setLoading(true);
            const response = await productAPI.getAllProducts();
            setProducts(response.data || []);
        } catch (error) {
            console.error('상품 목록 로딩 실패:', error);
        } finally {
            setLoading(false);
        }
    };

    const loadCategories = async () => {
        try {
            const response = await categoryAPI.getAllCategories();
            setCategories(response.data || []);
        } catch (error) {
            console.error('카테고리 로딩 실패:', error);
        }
    };

    const loadRowCategories = async (categoryId) => {
        try {
            const response = await adminProductAPI.getRowCategoriesByCategoryId(categoryId);
            console.log('관리자 하위 카테고리 응답:', response.data);
            setRowCategories(response.data || []);
        } catch (error) {
            console.error(
                '하위 카테고리 로딩 실패:',
                error.response?.status,
                error.response?.data || error.message
            );
            setRowCategories([]);
        }
    };

    const loadBrands = async () => {
        try {
            const response = await adminProductAPI.getAllBrands();
            console.log('관리자 브랜드 응답:', response.data);
            setBrands(response.data || []);
        } catch (error) {
            console.error(
                '브랜드 로딩 실패:',
                error.response?.status,
                error.response?.data || error.message
            );
        }
    };

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: value
        }));

        // 카테고리 선택 시 하위 카테고리 로드
        if (name === 'categoryId' && value) {
            loadRowCategories(value);
        }
    };

    const handleAddProduct = async (e) => {
        e.preventDefault();

        // 필수 필드 검증
        if (!formData.productName || !formData.productPrice || !formData.stockQuantity ||
            !formData.categoryId || !formData.rowCategoryId || !formData.brandId) {
            alert('필수 항목을 모두 입력해주세요.');
            return;
        }

        try {
            const productData = {
                productName: formData.productName,
                productPrice: parseInt(formData.productPrice),
                stockQuantity: parseInt(formData.stockQuantity),
                categoryId: parseInt(formData.categoryId),
                rowCategoryId: parseInt(formData.rowCategoryId),
                brandId: parseInt(formData.brandId),
                modelName: formData.modelName || null,
                capacity: formData.capacity || null,
                sizeInch: formData.sizeInch ? parseFloat(formData.sizeInch) : null,
                isInstallationRequired: formData.isInstallationRequired,
                productStatus: formData.productStatus,
                description: formData.description || null
            };

            console.log('등록할 상품 데이터:', productData);

            await adminProductAPI.createProduct(productData);
            alert('상품이 등록되었습니다.');
            setShowAddModal(false);
            resetForm();
            loadProducts();
        } catch (error) {
            console.error('상품 등록 실패:', error.response?.data || error);
            alert(error.response?.data?.message || '상품 등록에 실패했습니다.');
        }
    };

    const handleEditProduct = async (e) => {
        e.preventDefault();

        // 필수 필드 검증
        if (!formData.productName || !formData.productPrice || !formData.stockQuantity ||
            !formData.categoryId /*|| !formData.rowCategoryId || !formData.brandId*/) {
            alert('필수 항목을 모두 입력해주세요.');
            return;
        }

        try {
            const productData = {
                productName: formData.productName,
                productPrice: parseInt(formData.productPrice),
                stockQuantity: parseInt(formData.stockQuantity),
                categoryId: parseInt(formData.categoryId),
                rowCategoryId: parseInt(formData.rowCategoryId),
                brandId: parseInt(formData.brandId),
                modelName: formData.modelName || null,
                capacity: formData.capacity || null,
                sizeInch: formData.sizeInch ? parseFloat(formData.sizeInch) : null,
                isInstallationRequired: formData.isInstallationRequired,
                productStatus: formData.productStatus,
                description: formData.description || null
            };

            console.log('수정할 상품 데이터:', productData);

            await adminProductAPI.updateProduct(editingProduct.productId, productData);
            alert('상품이 수정되었습니다.');
            setShowEditModal(false);
            setEditingProduct(null);
            resetForm();
            loadProducts();
        } catch (error) {
            console.error('상품 수정 실패:', error.response?.data || error);
            alert(error.response?.data?.message || '상품 수정에 실패했습니다.');
        }
    };

    const handleDeleteProduct = async (productId, productName) => {
        if (!window.confirm(`${productName} 상품을 삭제하시겠습니까?`)) {
            return;
        }

        try {
            await adminProductAPI.deleteProduct(productId);
            alert('상품이 삭제되었습니다.');
            loadProducts();
        } catch (error) {
            console.error('상품 삭제 실패:', error);
            alert('상품 삭제에 실패했습니다.');
        }
    };

    const openEditModal = async (product) => {
        setEditingProduct(product);

        // 카테고리에 따른 하위 카테고리 로드
        if (product.categoryId) {
            await loadRowCategories(product.categoryId);
        }

        setFormData({
            productName: product.productName || '',
            productPrice: product.productPrice || '',
            stockQuantity: product.stockQuantity || '',
            categoryId: product.categoryId || '',
            rowCategoryId: product.rowCategoryId || '',
            brandId: product.brandId || '',
            modelName: product.modelName || '',
            capacity: product.capacity || '',
            sizeInch: product.sizeInch || '',
            isInstallationRequired: product.isInstallationRequired || 'N',
            productStatus: product.productStatus || 'ACTIVE',
            description: product.description || ''
        });
        setShowEditModal(true);
    };

    const resetForm = () => {
        setFormData({
            productName: '',
            productPrice: '',
            stockQuantity: '',
            categoryId: '',
            rowCategoryId: '',
            brandId: '',
            modelName: '',
            capacity: '',
            sizeInch: '',
            isInstallationRequired: 'N',
            productStatus: 'ACTIVE',
            description: ''
        });
        setRowCategories([]);
    };

    const ProductForm = ({ onSubmit, submitText }) => (
        <form onSubmit={onSubmit}>
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem', marginBottom: '1rem' }}>
                <div>
                    <label style={{ display: 'block', marginBottom: '0.25rem', fontSize: '0.875rem', fontWeight: '500' }}>
                        상품명 *
                    </label>
                    <input
                        type="text"
                        name="productName"
                        value={formData.productName}
                        onChange={handleInputChange}
                        required
                        style={{ width: '100%', padding: '0.5rem', border: '1px solid #d1d5db', borderRadius: '0.375rem' }}
                    />
                </div>

                <div>
                    <label style={{ display: 'block', marginBottom: '0.25rem', fontSize: '0.875rem', fontWeight: '500' }}>
                        가격 *
                    </label>
                    <input
                        type="number"
                        name="productPrice"
                        value={formData.productPrice}
                        onChange={handleInputChange}
                        required
                        min="0"
                        style={{ width: '100%', padding: '0.5rem', border: '1px solid #d1d5db', borderRadius: '0.375rem' }}
                    />
                </div>

                <div>
                    <label style={{ display: 'block', marginBottom: '0.25rem', fontSize: '0.875rem', fontWeight: '500' }}>
                        재고 수량 *
                    </label>
                    <input
                        type="number"
                        name="stockQuantity"
                        value={formData.stockQuantity}
                        onChange={handleInputChange}
                        required
                        min="0"
                        style={{ width: '100%', padding: '0.5rem', border: '1px solid #d1d5db', borderRadius: '0.375rem' }}
                    />
                </div>

                <div>
                    <label style={{ display: 'block', marginBottom: '0.25rem', fontSize: '0.875rem', fontWeight: '500' }}>
                        카테고리 *
                    </label>
                    <select
                        name="categoryId"
                        value={formData.categoryId}
                        onChange={handleInputChange}
                        required
                        style={{ width: '100%', padding: '0.5rem', border: '1px solid #d1d5db', borderRadius: '0.375rem' }}
                    >
                        <option value="">카테고리 선택</option>
                        {categories.map(cat => (
                            <option key={cat.categoryId} value={cat.categoryId}>
                                {cat.categoryName}
                            </option>
                        ))}
                    </select>
                </div>

                <div>
                    <label style={{ display: 'block', marginBottom: '0.25rem', fontSize: '0.875rem', fontWeight: '500' }}>
                        하위 카테고리 *
                    </label>
                    <select
                        name="rowCategoryId"
                        value={formData.rowCategoryId}
                        onChange={handleInputChange}
                        required
                        disabled={!formData.categoryId}
                        style={{ width: '100%', padding: '0.5rem', border: '1px solid #d1d5db', borderRadius: '0.375rem' }}
                    >
                        <option value="">
                            {formData.categoryId ? '하위 카테고리 선택' : '먼저 카테고리를 선택하세요'}
                        </option>
                        {rowCategories.map(cat => (
                            <option key={cat.rowCategoryId} value={cat.rowCategoryId}>
                                {cat.name}
                            </option>
                        ))}
                    </select>
                </div>

                <div>
                    <label style={{ display: 'block', marginBottom: '0.25rem', fontSize: '0.875rem', fontWeight: '500' }}>
                        브랜드 *
                    </label>
                    <select
                        name="brandId"
                        value={formData.brandId}
                        onChange={handleInputChange}
                        required
                        style={{ width: '100%', padding: '0.5rem', border: '1px solid #d1d5db', borderRadius: '0.375rem' }}
                    >
                        <option value="">브랜드 선택</option>
                        {brands.map(brand => (
                            <option key={brand.brandId} value={brand.brandId}>
                                {brand.name}
                            </option>
                        ))}
                    </select>
                </div>

                <div>
                    <label style={{ display: 'block', marginBottom: '0.25rem', fontSize: '0.875rem', fontWeight: '500' }}>
                        모델명
                    </label>
                    <input
                        type="text"
                        name="modelName"
                        value={formData.modelName}
                        onChange={handleInputChange}
                        style={{ width: '100%', padding: '0.5rem', border: '1px solid #d1d5db', borderRadius: '0.375rem' }}
                    />
                </div>

                <div>
                    <label style={{ display: 'block', marginBottom: '0.25rem', fontSize: '0.875rem', fontWeight: '500' }}>
                        용량
                    </label>
                    <input
                        type="text"
                        name="capacity"
                        value={formData.capacity}
                        onChange={handleInputChange}
                        placeholder="예: 500L"
                        style={{ width: '100%', padding: '0.5rem', border: '1px solid #d1d5db', borderRadius: '0.375rem' }}
                    />
                </div>

                <div>
                    <label style={{ display: 'block', marginBottom: '0.25rem', fontSize: '0.875rem', fontWeight: '500' }}>
                        크기(인치)
                    </label>
                    <input
                        type="number"
                        name="sizeInch"
                        value={formData.sizeInch}
                        onChange={handleInputChange}
                        step="0.1"
                        style={{ width: '100%', padding: '0.5rem', border: '1px solid #d1d5db', borderRadius: '0.375rem' }}
                    />
                </div>

                <div>
                    <label style={{ display: 'block', marginBottom: '0.25rem', fontSize: '0.875rem', fontWeight: '500' }}>
                        설치 필요
                    </label>
                    <select
                        name="isInstallationRequired"
                        value={formData.isInstallationRequired}
                        onChange={handleInputChange}
                        style={{ width: '100%', padding: '0.5rem', border: '1px solid #d1d5db', borderRadius: '0.375rem' }}
                    >
                        <option value="N">불필요</option>
                        <option value="Y">필요</option>
                    </select>
                </div>

                <div>
                    <label style={{ display: 'block', marginBottom: '0.25rem', fontSize: '0.875rem', fontWeight: '500' }}>
                        상품 상태
                    </label>
                    <select
                        name="productStatus"
                        value={formData.productStatus}
                        onChange={handleInputChange}
                        style={{ width: '100%', padding: '0.5rem', border: '1px solid #d1d5db', borderRadius: '0.375rem' }}
                    >
                        <option value="ACTIVE">판매중</option>
                        <option value="INACTIVE">판매중지</option>
                        <option value="OUT_OF_STOCK">품절</option>
                    </select>
                </div>
            </div>

            <div style={{ marginBottom: '1rem' }}>
                <label style={{ display: 'block', marginBottom: '0.25rem', fontSize: '0.875rem', fontWeight: '500' }}>
                    상품 설명
                </label>
                <textarea
                    name="description"
                    value={formData.description}
                    onChange={handleInputChange}
                    rows="4"
                    style={{ width: '100%', padding: '0.5rem', border: '1px solid #d1d5db', borderRadius: '0.375rem' }}
                />
            </div>

            <div style={{ display: 'flex', gap: '0.5rem', justifyContent: 'flex-end' }}>
                <button type="submit" className="btn btn--primary">
                    {submitText}
                </button>
                <button
                    type="button"
                    className="btn btn--ghost"
                    onClick={() => {
                        setShowAddModal(false);
                        setShowEditModal(false);
                        resetForm();
                    }}
                >
                    취소
                </button>
            </div>
        </form>
    );

    if (loading) {
        return (
            <div style={{ textAlign: 'center', padding: '3rem' }}>
                <p>로딩 중...</p>
            </div>
        );
    }

    return (
        <main className="main" style={{ gridTemplateColumns: '1fr', maxWidth: '1200px' }}>
            <div style={{ backgroundColor: '#ffffff', borderRadius: '1rem', padding: '2rem', border: '1px solid #e5e7eb' }}>
                {/* 헤더 */}
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '2rem' }}>
                    <div>
                        <h1 style={{ fontSize: '1.75rem', fontWeight: '700', marginBottom: '0.5rem' }}>
                            상품 관리
                        </h1>
                        <p style={{ color: '#6b7280', fontSize: '0.95rem' }}>
                            상품 등록, 수정, 삭제 관리
                        </p>
                    </div>
                    <div style={{ display: 'flex', gap: '0.5rem' }}>
                        <button
                            onClick={() => navigate('/admin')}
                            className="btn btn--ghost"
                        >
                            ← 대시보드
                        </button>
                        <button
                            onClick={() => {
                                resetForm();
                                setShowAddModal(true);
                            }}
                            className="btn btn--primary"
                        >
                            + 상품 등록
                        </button>
                    </div>
                </div>

                {/* 상품 목록 테이블 */}
                <div style={{ overflowX: 'auto' }}>
                    <table style={{ width: '100%', borderCollapse: 'collapse' }}>
                        <thead>
                        <tr style={{ borderBottom: '2px solid #e5e7eb' }}>
                            <th style={{ padding: '1rem', textAlign: 'left', fontWeight: '600' }}>ID</th>
                            <th style={{ padding: '1rem', textAlign: 'left', fontWeight: '600' }}>상품명</th>
                            <th style={{ padding: '1rem', textAlign: 'left', fontWeight: '600' }}>가격</th>
                            <th style={{ padding: '1rem', textAlign: 'left', fontWeight: '600' }}>재고</th>
                            <th style={{ padding: '1rem', textAlign: 'left', fontWeight: '600' }}>상태</th>
                            <th style={{ padding: '1rem', textAlign: 'center', fontWeight: '600' }}>관리</th>
                        </tr>
                        </thead>
                        <tbody>
                        {products.length === 0 ? (
                            <tr>
                                <td colSpan="6" style={{ padding: '2rem', textAlign: 'center', color: '#6b7280' }}>
                                    등록된 상품이 없습니다.
                                </td>
                            </tr>
                        ) : (
                            products.map((product) => (
                                <tr key={product.productId} style={{ borderBottom: '1px solid #f3f4f6' }}>
                                    <td style={{ padding: '1rem' }}>{product.productId}</td>
                                    <td style={{ padding: '1rem' }}>{product.productName}</td>
                                    <td style={{ padding: '1rem' }}>₩{product.productPrice?.toLocaleString()}</td>
                                    <td style={{ padding: '1rem' }}>{product.stockQuantity}</td>
                                    <td style={{ padding: '1rem' }}>
                                            <span style={{
                                                padding: '0.25rem 0.5rem',
                                                borderRadius: '0.375rem',
                                                fontSize: '0.75rem',
                                                backgroundColor: product.productStatus === 'ACTIVE' ? '#dcfce7' :
                                                    product.productStatus === 'OUT_OF_STOCK' ? '#fee2e2' : '#fef3c7',
                                                color: product.productStatus === 'ACTIVE' ? '#15803d' :
                                                    product.productStatus === 'OUT_OF_STOCK' ? '#b91c1c' : '#92400e'
                                            }}>
                                                {product.productStatus === 'ACTIVE' ? '판매중' :
                                                    product.productStatus === 'OUT_OF_STOCK' ? '품절' : '판매중지'}
                                            </span>
                                    </td>
                                    <td style={{ padding: '1rem', textAlign: 'center' }}>
                                        <button
                                            onClick={() => openEditModal(product)}
                                            className="btn btn--ghost"
                                            style={{ marginRight: '0.5rem', padding: '0.375rem 0.75rem', fontSize: '0.875rem' }}
                                        >
                                            수정
                                        </button>
                                        <button
                                            onClick={() => handleDeleteProduct(product.productId, product.productName)}
                                            className="btn btn--outline"
                                            style={{ padding: '0.375rem 0.75rem', fontSize: '0.875rem', borderColor: '#ef4444', color: '#ef4444' }}
                                        >
                                            삭제
                                        </button>
                                    </td>
                                </tr>
                            ))
                        )}
                        </tbody>
                    </table>
                </div>

                {/* 상품 등록 모달 */}
                {showAddModal && (
                    <div style={{
                        position: 'fixed',
                        top: 0,
                        left: 0,
                        right: 0,
                        bottom: 0,
                        backgroundColor: 'rgba(0, 0, 0, 0.5)',
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center',
                        zIndex: 1000
                    }}>
                        <div style={{
                            backgroundColor: '#ffffff',
                            borderRadius: '1rem',
                            padding: '2rem',
                            maxWidth: '800px',
                            width: '90%',
                            maxHeight: '90vh',
                            overflow: 'auto'
                        }}>
                            <h2 style={{ fontSize: '1.5rem', fontWeight: '700', marginBottom: '1.5rem' }}>
                                상품 등록
                            </h2>
                            <ProductForm onSubmit={handleAddProduct} submitText="등록" />
                        </div>
                    </div>
                )}

                {/* 상품 수정 모달 */}
                {showEditModal && (
                    <div style={{
                        position: 'fixed',
                        top: 0,
                        left: 0,
                        right: 0,
                        bottom: 0,
                        backgroundColor: 'rgba(0, 0, 0, 0.5)',
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center',
                        zIndex: 1000
                    }}>
                        <div style={{
                            backgroundColor: '#ffffff',
                            borderRadius: '1rem',
                            padding: '2rem',
                            maxWidth: '800px',
                            width: '90%',
                            maxHeight: '90vh',
                            overflow: 'auto'
                        }}>
                            <h2 style={{ fontSize: '1.5rem', fontWeight: '700', marginBottom: '1.5rem' }}>
                                상품 수정
                            </h2>
                            <ProductForm onSubmit={handleEditProduct} submitText="수정" />
                        </div>
                    </div>
                )}
            </div>
        </main>
    );
};

export default AdminProductPage;