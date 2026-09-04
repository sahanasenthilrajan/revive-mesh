"use client";

import { useState, useEffect } from "react";

interface DashboardMetrics {
  totalBudget: number;
  remainingBudget: number;
  recoveredRevenue: number;
  activeSwarms: number;
  pendingCases: number;
  blockedCases: number;
}

interface FailureSwarm {
  id: string;
  fingerprint: string;
  affectedCount: number;
  failureRate: number;
  baselineRate: number;
  severity: string;
  affectedDimensions: Record<string, string>;
  detectedAt: string;
}

interface RecoveryDecision {
  id: string;
  transactionId: string;
  recommendedAction: string;
  expectedNetValue: number;
  evaluations: ActionEvaluation[];
  createdAt: string;
}

interface ActionEvaluation {
  action: string;
  recoveryProbability: number;
  incrementalProbability: number;
  expectedIncrementalRevenue: number;
  actionCost: number;
  frictionCost: number;
  expectedNetValue: number;
}

export default function Home() {
  const [metrics, setMetrics] = useState<DashboardMetrics>({
    totalBudget: 500,
    remainingBudget: 500,
    recoveredRevenue: 0,
    activeSwarms: 0,
    pendingCases: 0,
    blockedCases: 0,
  });

  const [swarms, setSwarms] = useState<FailureSwarm[]>([]);
  const [decisions, setDecisions] = useState<RecoveryDecision[]>([]);
  const [selectedScenario, setSelectedScenario] = useState<number | null>(null);
  const [scenarioResult, setScenarioResult] = useState<{ ok: boolean; message: string } | null>(null);

  const apiBase = process.env.NEXT_PUBLIC_API_URL || "http://localhost:8080";

  useEffect(() => {
    fetchDashboardData();
    const interval = setInterval(fetchDashboardData, 5000);
    return () => clearInterval(interval);
  }, []);

  const fetchDashboardData = async () => {
    try {
      const [swarmsRes, decisionsRes] = await Promise.all([
        fetch(`${apiBase}/api/swarms/active`).catch(() => null),
        fetch(`${apiBase}/api/recovery/decisions/recent?limit=10`).catch(() => null),
      ]);

      if (swarmsRes && swarmsRes.ok) {
        const swarmsData = await swarmsRes.json();
        setSwarms(swarmsData);
        setMetrics((m) => ({ ...m, activeSwarms: swarmsData.length }));
      }

      if (decisionsRes && decisionsRes.ok) {
        const decisionsData = await decisionsRes.json();
        setDecisions(decisionsData);
      }
    } catch (error) {
      console.error("Failed to fetch dashboard data:", error);
    }
  };

  const runScenario = async (scenarioNum: number) => {
    setSelectedScenario(scenarioNum);
    setScenarioResult(null);
    try {
      const res = await fetch(`${apiBase}/api/simulator/scenario/${scenarioNum}`, {
        method: "POST",
      });
      if (res.ok) {
        const data = (await res.json()) as { status?: string; scenario?: string };
        setScenarioResult({
          ok: true,
          message: `Scenario ${scenarioNum} complete: ${data.scenario ?? "Unknown"} (status: ${data.status ?? "unknown"})`,
        });
        setTimeout(fetchDashboardData, 2000);
      } else {
        setScenarioResult({
          ok: false,
          message: `Scenario ${scenarioNum} failed (HTTP ${res.status}). Please try again.`,
        });
      }
    } catch (error) {
      console.error(`Failed to run scenario ${scenarioNum}:`, error);
      setScenarioResult({
        ok: false,
        message: `Scenario ${scenarioNum} failed: could not reach the backend. Check your connection and try again.`,
      });
    } finally {
      setSelectedScenario(null);
    }
  };

  const formatCurrency = (amount: number) =>
    new Intl.NumberFormat("en-US", { style: "currency", currency: "USD" }).format(amount);

  const formatPercent = (value: number) => `${(value * 100).toFixed(2)}%`;

  return (
    <div className="min-h-screen bg-zinc-950 text-zinc-50 font-sans">
      {/* Header */}
      <header className="border-b border-zinc-800 bg-zinc-900">
        <div className="max-w-7xl mx-auto px-6 py-6">
          <h1 className="text-3xl font-bold tracking-tight">REVIVE MESH</h1>
          <p className="text-sm text-zinc-400 mt-1">
            Incident-Aware Revenue Recovery Control Plane
          </p>
        </div>
      </header>

      <main className="max-w-7xl mx-auto px-6 py-8 space-y-8">
        {/* Executive Metrics */}
        <section>
          <h2 className="text-xl font-semibold mb-4 text-zinc-100">War Room</h2>
          <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-6 gap-4">
            <MetricCard label="Recovery Budget" value={formatCurrency(metrics.totalBudget)} />
            <MetricCard
              label="Remaining"
              value={formatCurrency(metrics.remainingBudget)}
              trend="neutral"
            />
            <MetricCard
              label="Recovered Revenue"
              value={formatCurrency(metrics.recoveredRevenue)}
              trend="positive"
            />
            <MetricCard label="Active Swarms" value={metrics.activeSwarms.toString()} />
            <MetricCard label="Pending Cases" value={metrics.pendingCases.toString()} />
            <MetricCard label="Blocked Cases" value={metrics.blockedCases.toString()} />
          </div>
        </section>

        {/* Demo Scenarios */}
        <section className="bg-zinc-900 rounded-lg border border-zinc-800 p-6">
          <h2 className="text-lg font-semibold mb-4 text-zinc-100">Demo Scenarios</h2>
          <div className="grid grid-cols-1 md:grid-cols-5 gap-3">
            <ScenarioButton
              num={1}
              label="Normal Failures"
              onClick={() => runScenario(1)}
              loading={selectedScenario === 1}
            />
            <ScenarioButton
              num={2}
              label="Failure Swarm"
              onClick={() => runScenario(2)}
              loading={selectedScenario === 2}
            />
            <ScenarioButton
              num={3}
              label="Budget Competition"
              onClick={() => runScenario(3)}
              loading={selectedScenario === 3}
            />
            <ScenarioButton
              num={4}
              label="DO_NOTHING Wins"
              onClick={() => runScenario(4)}
              loading={selectedScenario === 4}
            />
            <ScenarioButton
              num={5}
              label="Intervention Wins"
              onClick={() => runScenario(5)}
              loading={selectedScenario === 5}
            />
          </div>
          {selectedScenario !== null ? (
            <p className="mt-4 text-sm text-zinc-400">
              Running Scenario {selectedScenario}...
            </p>
          ) : scenarioResult ? (
            <p
              className={`mt-4 text-sm ${
                scenarioResult.ok ? "text-emerald-400" : "text-red-400"
              }`}
            >
              {scenarioResult.message}
            </p>
          ) : null}
        </section>

        {/* Active Failure Swarms */}
        {swarms.length > 0 && (
          <section className="bg-zinc-900 rounded-lg border border-zinc-800 p-6">
            <h2 className="text-lg font-semibold mb-4 text-zinc-100">Active Failure Swarms</h2>
            <div className="space-y-4">
              {swarms.map((swarm) => (
                <div key={swarm.id} className="border border-zinc-700 rounded p-4 space-y-2">
                  <div className="flex justify-between items-start">
                    <div>
                      <p className="font-mono text-sm text-zinc-400">{swarm.fingerprint}</p>
                      <p className="text-sm text-zinc-300 mt-1">
                        {swarm.affectedCount} failures • {formatPercent(swarm.failureRate)} vs{" "}
                        {formatPercent(swarm.baselineRate)} baseline
                      </p>
                    </div>
                    <span
                      className={`px-2 py-1 rounded text-xs font-medium ${
                        swarm.severity === "HIGH"
                          ? "bg-red-900 text-red-200"
                          : "bg-yellow-900 text-yellow-200"
                      }`}
                    >
                      {swarm.severity}
                    </span>
                  </div>
                  <div className="text-xs text-zinc-400">
                    {Object.entries(swarm.affectedDimensions).map(([key, value]) => (
                      <span key={key} className="mr-3">
                        {key}: {value}
                      </span>
                    ))}
                  </div>
                </div>
              ))}
            </div>
          </section>
        )}

        {/* Recent Counterfactual Decisions */}
        {decisions.length > 0 && (
          <section className="bg-zinc-900 rounded-lg border border-zinc-800 p-6">
            <h2 className="text-lg font-semibold mb-4 text-zinc-100">
              Recent Counterfactual Decisions
            </h2>
            <div className="space-y-6">
              {decisions.slice(0, 3).map((decision) => (
                <div key={decision.id} className="border border-zinc-700 rounded p-4">
                  <div className="flex justify-between items-center mb-3">
                    <p className="font-mono text-sm text-zinc-400">
                      Transaction {decision.transactionId.slice(0, 8)}...
                    </p>
                    <span className="px-3 py-1 bg-emerald-900 text-emerald-200 rounded text-sm font-medium">
                      {decision.recommendedAction}
                    </span>
                  </div>
                  <div className="overflow-x-auto">
                    <table className="w-full text-sm">
                      <thead>
                        <tr className="border-b border-zinc-700 text-zinc-400 text-left">
                          <th className="py-2 pr-4">Action</th>
                          <th className="py-2 pr-4">P(recovery)</th>
                          <th className="py-2 pr-4">Δ P</th>
                          <th className="py-2 pr-4">Exp. Rev</th>
                          <th className="py-2 pr-4">Cost</th>
                          <th className="py-2 pr-4">ENR</th>
                        </tr>
                      </thead>
                      <tbody>
                        {decision.evaluations.map((eval_item) => (
                          <tr
                            key={eval_item.action}
                            className={`border-b border-zinc-800 ${
                              eval_item.action === decision.recommendedAction
                                ? "bg-emerald-950"
                                : eval_item.action === "DO_NOTHING"
                                ? "bg-zinc-800"
                                : ""
                            }`}
                          >
                            <td className="py-2 pr-4 font-medium">{eval_item.action}</td>
                            <td className="py-2 pr-4">
                              {formatPercent(eval_item.recoveryProbability)}
                            </td>
                            <td className="py-2 pr-4">
                              {formatPercent(eval_item.incrementalProbability)}
                            </td>
                            <td className="py-2 pr-4">
                              {formatCurrency(eval_item.expectedIncrementalRevenue)}
                            </td>
                            <td className="py-2 pr-4">
                              {formatCurrency(eval_item.actionCost + eval_item.frictionCost)}
                            </td>
                            <td className="py-2 pr-4 font-semibold">
                              {formatCurrency(eval_item.expectedNetValue)}
                            </td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  </div>
                </div>
              ))}
            </div>
          </section>
        )}
      </main>
    </div>
  );
}

function MetricCard({
  label,
  value,
  trend,
}: {
  label: string;
  value: string;
  trend?: "positive" | "negative" | "neutral";
}) {
  return (
    <div className="bg-zinc-900 border border-zinc-800 rounded-lg p-4">
      <p className="text-xs text-zinc-400 uppercase tracking-wider mb-1">{label}</p>
      <p
        className={`text-2xl font-bold ${
          trend === "positive"
            ? "text-emerald-400"
            : trend === "negative"
            ? "text-red-400"
            : "text-zinc-50"
        }`}
      >
        {value}
      </p>
    </div>
  );
}

function ScenarioButton({
  num,
  label,
  onClick,
  loading,
}: {
  num: number;
  label: string;
  onClick: () => void;
  loading: boolean;
}) {
  return (
    <button
      onClick={onClick}
      disabled={loading}
      className="bg-zinc-800 hover:bg-zinc-700 disabled:bg-zinc-850 border border-zinc-700 rounded px-4 py-3 text-sm font-medium transition-colors disabled:opacity-50"
    >
      {loading ? "Running..." : `${num}. ${label}`}
    </button>
  );
}
