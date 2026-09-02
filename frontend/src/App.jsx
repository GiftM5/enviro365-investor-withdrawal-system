import React, { useEffect, useMemo, useState } from 'react';
import { BrowserRouter, Navigate, Route, Routes, Link, useNavigate, useParams } from 'react-router-dom';
import axios from 'axios';

const API_BASE = 'http://localhost:8080/api';

const currency = (value) =>
  new Intl.NumberFormat('en-ZA', {
    style: 'currency',
    currency: 'ZAR',
    minimumFractionDigits: 2,
    maximumFractionDigits: 2,
  }).format(Number(value || 0));

const formatDate = (value) => {
  if (!value) return '—';
  const date = new Date(value);
  return isNaN(date.getTime()) ? value : date.toLocaleDateString('en-ZA', { day: '2-digit', month: 'short', year: 'numeric' });
};

const calculateAge = (dateOfBirth) => {
  if (!dateOfBirth) return '—';
  const diff = new Date(Date.now() - new Date(dateOfBirth).getTime());
  return Math.abs(diff.getUTCFullYear() - 1970);
};

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<InvestorDirectoryPage />} />
        <Route path="/investor/:investorId/dashboard" element={<InvestorDashboardPage />} />
        <Route path="/investor/:investorId/withdrawals" element={<WithdrawalHistoryPage />} />
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </BrowserRouter>
  );
}

function InvestorDirectoryPage() {
  const [investors, setInvestors] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const navigate = useNavigate();

  useEffect(() => {
    let mounted = true;
    axios.get(`${API_BASE}/investors`)
      .then((response) => {
        if (mounted) {
          setInvestors(response.data || []);
          setLoading(false);
        }
      })
      .catch(() => {
        if (mounted) {
          setError('Unable to load investors. Please try again.');
          setLoading(false);
        }
      });

    return () => { mounted = false; };
  }, []);

  if (loading) {
    return <Shell><div className="state-box">Loading investors...</div></Shell>;
  }

  if (error) {
    return <Shell><div className="state-box error"><strong>Unable to load investor directory.</strong><div>{error}</div><button onClick={() => window.location.reload()}>Retry</button></div></Shell>;
  }

  return (
    <Shell>
      <div className="directory-header">
        <div>
          <p className="eyebrow">Enviro365</p>
          <h1>Investor Portal</h1>
        </div>
      </div>

      <div className="directory-panel">
        <h2>Select an investor</h2>
        <p className="muted">Choose an investor to view their portfolio and withdrawal history.</p>

        <div className="investor-grid">
          {investors.map((investor) => (
            <div className="investor-card" key={investor.investorId}>
              <div className="investor-card__body">
                <h3>{investor.fullName}</h3>
                <p>{investor.investorId ? `INV-${String(investor.investorId).padStart(3, '0')}` : 'INV-000'}</p>
              </div>
              <button className="primary" onClick={() => navigate(`/investor/${investor.investorId}/dashboard`)}>
                View Portfolio
              </button>
            </div>
          ))}
        </div>
      </div>
    </Shell>
  );
}

function InvestorDashboardPage() {
  const { investorId } = useParams();
  const [investor, setInvestor] = useState(null);
  const [products, setProducts] = useState([]);
  const [history, setHistory] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [selectedProduct, setSelectedProduct] = useState(null);
  const [showForm, setShowForm] = useState(false);
  const [withdrawalInput, setWithdrawalInput] = useState('');
  const [withFormError, setWithFormError] = useState('');
  const [confirmOpen, setConfirmOpen] = useState(false);
  const [processing, setProcessing] = useState(false);
  const [successMessage, setSuccessMessage] = useState(null);
  const navigate = useNavigate();

  const loadInvestorData = () => {
    setLoading(true);
    setError('');

    Promise.all([
      axios.get(`${API_BASE}/investors/${investorId}/portfolio`),
      axios.get(`${API_BASE}/investors/${investorId}/withdrawals`)
    ])
      .then(([portfolioRes, historyRes]) => {
        const data = portfolioRes.data;
        setInvestor({
          investorId: data.investorId,
          investorName: data.investorName,
          portfolioNumber: data.portfolioNumber,
          age: calculateAge(data.dateOfBirth)
        });
        setProducts(data.products || []);
        setHistory((historyRes.data || []).slice(0, 5));
        setLoading(false);
      })
      .catch(() => {
        setError('Unable to load portfolio. Please try again.');
        setLoading(false);
      });
  };

  useEffect(() => {
    loadInvestorData();
  }, [investorId]);

  const totalValue = useMemo(
    () => products.reduce((sum, p) => sum + Number(p.balance || 0), 0),
    [products]
  );

  const startWithdrawal = (product) => {
    setSelectedProduct(product);
    setShowForm(true);
    setWithdrawalInput('');
    setWithFormError('');
    setConfirmOpen(false);
  };

  const validateWithdrawalAmount = (rawValue) => {
    const parsed = Number(rawValue);
    if (!rawValue || rawValue.trim() === '') {
      return 'Please enter a withdrawal amount.';
    }
    if (Number.isNaN(parsed) || parsed <= 0) {
      return 'Withdrawal amount must be greater than zero.';
    }
    if (parsed > Number(selectedProduct.balance)) {
      return 'Withdrawal amount cannot exceed the available balance.';
    }
    if (parsed > Number(selectedProduct.maximumWithdrawal)) {
      return 'Withdrawal amount cannot exceed 90% of the available balance.';
    }
    return '';
  };

  const continueToConfirmation = () => {
    const error = validateWithdrawalAmount(withdrawalInput);
    setWithFormError(error);
    if (!error) {
      setConfirmOpen(true);
    }
  };

  const submitWithdrawal = () => {
    if (!selectedProduct || !withdrawalInput) return;

    const error = validateWithdrawalAmount(withdrawalInput);
    if (error) {
      setWithFormError(error);
      return;
    }

    setProcessing(true);
    axios.post(`${API_BASE}/withdrawals`, {
      investorId: Number(investorId),
      productId: selectedProduct.productId,
      amount: Number(withdrawalInput)
    })
      .then((response) => {
        const result = response.data;
        setSuccessMessage({
          withdrawalId: result.withdrawalId,
          productName: selectedProduct.productName,
          amount: result.amount,
          remainingBalance: result.remainingBalance,
          createdAt: result.createdAt
        });
        setConfirmOpen(false);
        setShowForm(false);
        setSelectedProduct(null);
        setWithdrawalInput('');
        setWithFormError('');
        loadInvestorData();
      })
      .catch((apiError) => {
        const msg = apiError?.response?.data?.message || 'This withdrawal could not be processed.';
        setConfirmOpen(false);
        setShowForm(true);
        setWithFormError(msg);
      })
      .finally(() => {
        setProcessing(false);
      });
  };

  const remainingPreview = () => {
    if (!selectedProduct || !withdrawalInput) {
      return Number(selectedProduct?.balance || 0);
    }
    return Number(selectedProduct.balance) - Number(withdrawalInput || 0);
  };

  if (loading) {
    return <Shell><div className="state-box">Loading your portfolio...</div></Shell>;
  }

  if (error) {
    return <Shell><div className="state-box error"><strong>Unable to load portfolio.</strong><div>{error}</div><button onClick={loadInvestorData}>Retry</button></div></Shell>;
  }

  return (
    <Shell>
      <div className="topbar-row">
        <div>
          <p className="eyebrow">Enviro365</p>
          <h1>Welcome to {investor?.investorName?.split(' ')[0] || 'Investor'}'s Portfolio</h1>
        </div>
        <div className="header-actions">
          <Link to={`/investor/${investorId}/withdrawals`} className="text-link">Withdrawals</Link>
          <button className="secondary" onClick={() => navigate('/')}>Change Investor</button>
        </div>
      </div>

      <div className="stats-grid">
        <StatCard label="Total portfolio" value={currency(totalValue)} />
        <StatCard label="Products" value={String(products.length)} />
        <StatCard label="Withdrawals" value={String(history.length)} />
      </div>

      <div className="panel">
        <div className="panel-header">
          <h2>Current Investments</h2>
        </div>

        {products.length === 0 ? (
          <div className="empty-box">No investment products found.</div>
        ) : (
          <div className="product-list">
            {products.map((product) => (
              <div className="product-item" key={product.productId}>
                <div>
                  <h3>{product.productName}</h3>
                  <p>{product.productType} • Active</p>
                </div>
                <div className="product-meta">
                  <strong>{currency(product.balance)}</strong>
                  <button className="secondary" onClick={() => setSelectedProduct(product)}>View Details</button>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>

      <div className="panel">
        <div className="panel-header">
          <h2>Recent Withdrawals</h2>
        </div>

        {history.length === 0 ? (
          <div className="empty-box">No withdrawal notices yet.</div>
        ) : (
          <table>
            <thead>
              <tr>
                <th>Date</th>
                <th>Product</th>
                <th>Amount</th>
                <th>Status</th>
              </tr>
            </thead>
            <tbody>
              {history.map((entry) => (
                <tr key={entry.id}>
                  <td>{formatDate(entry.createdAt)}</td>
                  <td>{entry.productId}</td>
                  <td>{currency(entry.amount)}</td>
                  <td>{entry.status}</td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>

      {selectedProduct && (
        <div className="modal-backdrop" onClick={() => setSelectedProduct(null)}>
          <div className="modal" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h3>{selectedProduct.productName}</h3>
              <button className="close-btn" onClick={() => setSelectedProduct(null)}>×</button>
            </div>
            <div className="modal-body">
              <div className="labeled-row"><span>Type</span><strong>{selectedProduct.productType}</strong></div>
              <div className="labeled-row"><span>Current Balance</span><strong>{currency(selectedProduct.balance)}</strong></div>
              <div className="labeled-row"><span>Maximum Withdrawal</span><strong>{currency(selectedProduct.maximumWithdrawal)}</strong></div>
              <div className="labeled-row"><span>Status</span><strong>Active</strong></div>
            </div>
            <div className="modal-footer">
              <button className="secondary" onClick={() => setSelectedProduct(null)}>Close</button>
              <button className="primary" onClick={() => startWithdrawal(selectedProduct)}>Start Withdrawal</button>
            </div>
          </div>
        </div>
      )}

      {showForm && selectedProduct && (
        <div className="modal-backdrop" onClick={() => setShowForm(false)}>
          <div className="modal" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h3>Withdraw from {selectedProduct.productName}</h3>
              <button className="close-btn" onClick={() => setShowForm(false)}>×</button>
            </div>
            <div className="modal-body">
              <div className="labeled-row"><span>Available Balance</span><strong>{currency(selectedProduct.balance)}</strong></div>
              <div className="labeled-row"><span>Maximum Withdrawal</span><strong>{currency(selectedProduct.maximumWithdrawal)}</strong></div>

              <label className="field-label">Withdrawal Amount</label>
              <input
                type="number"
                value={withdrawalInput}
                min="0"
                step="0.01"
                onChange={(e) => {
                  setWithdrawalInput(e.target.value);
                  setWithFormError(validateWithdrawalAmount(e.target.value));
                }}
                placeholder="0.00"
              />

              <div className="preview-row">
                <span>Remaining Balance</span>
                <strong>{currency(remainingPreview())}</strong>
              </div>

              {withFormError && <div className="inline-error">{withFormError}</div>}
            </div>
            <div className="modal-footer">
              <button className="secondary" onClick={() => setShowForm(false)}>Cancel</button>
              <button className="primary" disabled={Boolean(withFormError) || !withdrawalInput} onClick={continueToConfirmation}>Continue</button>
            </div>
          </div>
        </div>
      )}

      {confirmOpen && selectedProduct && (
        <div className="modal-backdrop" onClick={() => setConfirmOpen(false)}>
          <div className="modal" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h3>Confirm Withdrawal</h3>
              <button className="close-btn" onClick={() => setConfirmOpen(false)}>×</button>
            </div>
            <div className="modal-body confirm-body">
              <div className="confirm-row"><span>Investor</span><strong>{investor?.investorName}</strong></div>
              <div className="confirm-row"><span>Product</span><strong>{selectedProduct.productName}</strong></div>
              <div className="confirm-row"><span>Current Balance</span><strong>{currency(selectedProduct.balance)}</strong></div>
              <div className="confirm-row"><span>Withdrawal Amount</span><strong>{currency(withdrawalInput)}</strong></div>
              <div className="confirm-row"><span>Remaining Balance</span><strong>{currency(Number(selectedProduct.balance) - Number(withdrawalInput))}</strong></div>
            </div>
            <div className="modal-footer">
              <button className="secondary" onClick={() => setConfirmOpen(false)}>Cancel</button>
              <button className="primary" onClick={submitWithdrawal} disabled={processing}>
                {processing ? 'Processing...' : 'Confirm Withdrawal'}
              </button>
            </div>
          </div>
        </div>
      )}

      {successMessage && (
        <div className="modal-backdrop" onClick={() => setSuccessMessage(null)}>
          <div className="modal success-modal" onClick={(e) => e.stopPropagation()}>
            <div className="success-icon">✓</div>
            <h3>Withdrawal Submitted</h3>
            <p>The withdrawal notice has been successfully submitted.</p>
            <div className="confirm-row"><span>Reference</span><strong>{successMessage.withdrawalId}</strong></div>
            <div className="confirm-row"><span>Product</span><strong>{successMessage.productName}</strong></div>
            <div className="confirm-row"><span>Amount</span><strong>{currency(successMessage.amount)}</strong></div>
            <div className="confirm-row"><span>Remaining Balance</span><strong>{currency(successMessage.remainingBalance)}</strong></div>
            <button className="primary" onClick={() => setSuccessMessage(null)}>Done</button>
          </div>
        </div>
      )}
    </Shell>
  );
}

function WithdrawalHistoryPage() {
  const { investorId } = useParams();
  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [filters, setFilters] = useState({ productId: '', from: '', to: '', status: '' });
  const navigate = useNavigate();

  const loadHistory = () => {
    setLoading(true);
    axios.get(`${API_BASE}/investors/${investorId}/withdrawals`)
      .then((response) => {
        setItems(response.data || []);
        setLoading(false);
      })
      .catch(() => {
        setError('Unable to load withdrawal history.');
        setLoading(false);
      });
  };

  useEffect(() => {
    loadHistory();
  }, [investorId]);

  const downloadCsv = () => {
    const params = new URLSearchParams();
    if (filters.productId) params.set('productId', filters.productId);
    if (filters.from) params.set('from', filters.from);
    if (filters.to) params.set('to', filters.to);
    if (filters.status) params.set('status', filters.status);
    window.open(`${API_BASE}/investors/${investorId}/withdrawals/export?${params.toString()}`, '_blank');
  };

  if (loading) {
    return <Shell><div className="state-box">Loading withdrawal history...</div></Shell>;
  }

  if (error) {
    return <Shell><div className="state-box error"><strong>Unable to load withdrawal history.</strong><div>{error}</div><button onClick={loadHistory}>Retry</button></div></Shell>;
  }

  return (
    <Shell>
      <div className="topbar-row">
        <div>
          <p className="eyebrow">Enviro365</p>
          <h1>Withdrawal History</h1>
        </div>
        <div className="header-actions">
          <button className="secondary" onClick={() => navigate(`/investor/${investorId}/dashboard`)}>Back to Dashboard</button>
        </div>
      </div>

      <div className="panel filter-panel">
        <div className="filter-grid">
          <div>
            <label>Product</label>
            <select value={filters.productId} onChange={(e) => setFilters({ ...filters, productId: e.target.value })}>
              <option value="">All Products</option>
              <option value="1">Retirement Annuity</option>
              <option value="2">Growth Fund</option>
              <option value="3">Education Reserve</option>
            </select>
          </div>
          <div>
            <label>From</label>
            <input type="date" value={filters.from} onChange={(e) => setFilters({ ...filters, from: e.target.value })} />
          </div>
          <div>
            <label>To</label>
            <input type="date" value={filters.to} onChange={(e) => setFilters({ ...filters, to: e.target.value })} />
          </div>
          <div>
            <label>Status</label>
            <select value={filters.status} onChange={(e) => setFilters({ ...filters, status: e.target.value })}>
              <option value="">All Statuses</option>
              <option value="APPROVED">Approved</option>
            </select>
          </div>
        </div>

        <div className="filter-actions">
          <button className="secondary" onClick={() => setFilters({ productId: '', from: '', to: '', status: '' })}>Clear</button>
          <button className="primary" onClick={downloadCsv}>Download CSV</button>
        </div>
      </div>

      <div className="panel">
        {items.length === 0 ? (
          <div className="empty-box">No withdrawal notices found.</div>
        ) : (
          <table>
            <thead>
              <tr>
                <th>Date</th>
                <th>Product</th>
                <th>Amount</th>
                <th>Previous Balance</th>
                <th>Remaining Balance</th>
                <th>Status</th>
              </tr>
            </thead>
            <tbody>
              {items.map((item) => (
                <tr key={item.id}>
                  <td>{formatDate(item.createdAt)}</td>
                  <td>{item.productId}</td>
                  <td>{currency(item.amount)}</td>
                  <td>{currency(item.previousBalance)}</td>
                  <td>{currency(item.remainingBalance)}</td>
                  <td>{item.status}</td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </Shell>
  );
}

function Shell({ children }) {
  return <div className="app-shell">{children}</div>;
}

function StatCard({ label, value }) {
  return (
    <div className="stat-card">
      <p>{label}</p>
      <strong>{value}</strong>
    </div>
  );
}

export default App;
