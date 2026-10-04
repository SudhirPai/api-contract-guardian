import { useState } from "react";
import { Routes, Route, Link, useParams } from "react-router-dom";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { api, MonitoredApi, Change, Impact } from "./api";
import {
  AppBar,
  Toolbar,
  Typography,
  Container,
  Stack,
  Button,
  Card,
  CardContent,
  Grid2 as Grid,
  Chip,
  Table,
  TableHead,
  TableRow,
  TableCell,
  TableBody,
  Paper,
  Alert,
  LinearProgress,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  IconButton,
} from "@mui/material";
import RefreshIcon from "@mui/icons-material/Refresh";
import DeleteIcon from "@mui/icons-material/Delete";

const tone = (v: string) =>
  v === "CRITICAL" || v === "BREAKING"
    ? "error"
    : v === "HIGH" || v === "POTENTIALLY_BREAKING"
      ? "warning"
      : v === "MEDIUM"
        ? "info"
        : "success";
const Badge = ({ children }: { children: string }) => (
  <Chip
    size="small"
    label={children.replaceAll("_", " ")}
    color={tone(children) as "error"}
  />
);

function Shell({ children }: { children: React.ReactNode }) {
  return (
    <>
      <AppBar position="sticky" elevation={0}>
        <Toolbar>
          <Typography
            component={Link}
            to="/"
            color="inherit"
            sx={{
              textDecoration: "none",
              fontWeight: 800,
              fontSize: 20,
              flexGrow: 1,
            }}
          >
            API Contract Guardian
          </Typography>
          <Button component={Link} to="/mappings" color="inherit">
            Mappings
          </Button>
          <Button component={Link} to="/apis" color="inherit">
            APIs
          </Button>
        </Toolbar>
      </AppBar>
      <Container maxWidth="xl" sx={{ py: 4 }}>
        {children}
      </Container>
    </>
  );
}

function Stat({
  label,
  value,
  accent,
}: {
  label: string;
  value: number;
  accent?: string;
}) {
  return (
    <Card>
      <CardContent>
        <Typography variant="body2" color="text.secondary">
          {label}
        </Typography>
        <Typography variant="h3" sx={{ fontWeight: 750, color: accent }}>
          {value}
        </Typography>
      </CardContent>
    </Card>
  );
}

function Dashboard() {
  const q = useQuery({
    queryKey: ["summary"],
    queryFn: async () => (await api.get("/dashboard/summary")).data,
  });
  const qc = useQueryClient();
  const advance = useMutation({
    mutationFn: async () => api.post("/apis/1/demo/v2"),
    onSuccess: () => qc.invalidateQueries(),
  });
  if (q.isLoading) return <LinearProgress />;
  if (q.isError)
    return <Alert severity="error">Could not load dashboard.</Alert>;
  const s = q.data;
  return (
    <Stack spacing={3}>
      <Stack direction="row" justifyContent="space-between" alignItems="center">
        <div>
          <Typography variant="h4" fontWeight={800}>
            Contract risk overview
          </Typography>
          <Typography color="text.secondary">
            Monitor upstream changes before they reach your consumers.
          </Typography>
        </div>
        <Button
          variant="contained"
          onClick={() => advance.mutate()}
          disabled={advance.isPending}
        >
          Run demo: compare v2
        </Button>
      </Stack>
      <Grid container spacing={2}>
        <Grid size={{ xs: 6, md: 3 }}>
          <Stat label="Monitored APIs" value={s.monitoredApis} />
        </Grid>
        <Grid size={{ xs: 6, md: 3 }}>
          <Stat
            label="High / critical impacts"
            value={s.highCriticalImpacts}
            accent="#D32F2F"
          />
        </Grid>
        <Grid size={{ xs: 6, md: 3 }}>
          <Stat label="Open impacts" value={s.unresolvedImpacts} />
        </Grid>
        <Grid size={{ xs: 6, md: 3 }}>
          <Stat label="Consumer applications" value={s.totalConsumers} />
        </Grid>
      </Grid>
      <Paper>
        <Typography variant="h6" p={2} fontWeight={700}>
          Recent contract changes
        </Typography>
        <ChangeTable changes={s.recentChanges} />
      </Paper>
    </Stack>
  );
}

function ChangeTable({ changes }: { changes: Change[] }) {
  return (
    <Table size="small">
      <TableHead>
        <TableRow>
          <TableCell>Severity</TableCell>
          <TableCell>Change</TableCell>
          <TableCell>Endpoint</TableCell>
          <TableCell>JSON path</TableCell>
          <TableCell>Confidence</TableCell>
        </TableRow>
      </TableHead>
      <TableBody>
        {changes?.length ? (
          changes.map((c) => (
            <TableRow key={c.id} hover>
              <TableCell>
                <Badge>{c.severity}</Badge>
              </TableCell>
              <TableCell>{c.changeType.replaceAll("_", " ")}</TableCell>
              <TableCell>{c.endpoint}</TableCell>
              <TableCell sx={{ fontFamily: "monospace" }}>
                {c.jsonPath}
              </TableCell>
              <TableCell>{c.confidence}%</TableCell>
            </TableRow>
          ))
        ) : (
          <TableRow>
            <TableCell colSpan={5} align="center">
              No contract changes yet. Run the demo comparison.
            </TableCell>
          </TableRow>
        )}
      </TableBody>
    </Table>
  );
}

function Apis() {
  const q = useQuery<MonitoredApi[]>({
    queryKey: ["apis"],
    queryFn: async () => (await api.get("/apis")).data,
  });
  return (
    <Stack spacing={2}>
      <Typography variant="h4" fontWeight={800}>
        Monitored APIs
      </Typography>
      <Paper>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>Name</TableCell>
              <TableCell>Owner</TableCell>
              <TableCell>Environment</TableCell>
              <TableCell>Last checked</TableCell>
              <TableCell />
            </TableRow>
          </TableHead>
          <TableBody>
            {q.data?.map((a) => (
              <TableRow key={a.id}>
                <TableCell>
                  <Button component={Link} to={`/apis/${a.id}`}>
                    {a.name}
                  </Button>
                  <Typography variant="caption" display="block">
                    {a.swaggerUrl}
                  </Typography>
                </TableCell>
                <TableCell>
                  {a.application} · {a.team}
                </TableCell>
                <TableCell>
                  <Badge>{a.environment}</Badge>
                </TableCell>
                <TableCell>{new Date(a.updatedAt).toLocaleString()}</TableCell>
                <TableCell>
                  <Button component={Link} to={`/apis/${a.id}`}>
                    Details
                  </Button>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </Paper>
    </Stack>
  );
}

function ApiDetails() {
  const { id = "" } = useParams();
  const qc = useQueryClient();
  const a = useQuery<MonitoredApi>({
    queryKey: ["api", id],
    queryFn: async () => (await api.get(`/apis/${id}`)).data,
  });
  const c = useQuery<Change[]>({
    queryKey: ["changes", id],
    queryFn: async () => (await api.get(`/apis/${id}/changes`)).data,
  });
  const impacts = useQuery<Impact[]>({
    queryKey: ["impacts", id],
    queryFn: async () => (await api.get(`/apis/${id}/impacts`)).data,
  });
  const check = useMutation({
    mutationFn: async () => api.post(`/apis/${id}/check`),
    onSuccess: () => qc.invalidateQueries(),
  });
  if (!a.data) return <LinearProgress />;
  return (
    <Stack spacing={3}>
      <Stack direction="row" justifyContent="space-between">
        <div>
          <Typography variant="h4" fontWeight={800}>
            {a.data.name}
          </Typography>
          <Typography color="text.secondary">
            {a.data.team} · {a.data.environment}
          </Typography>
        </div>
        <Button
          variant="contained"
          startIcon={<RefreshIcon />}
          onClick={() => check.mutate()}
        >
          Check now
        </Button>
      </Stack>
      <Grid container spacing={2}>
        <Grid size={{ xs: 12, md: 5 }}>
          <Card>
            <CardContent>
              <Typography variant="subtitle2">Source URL</Typography>
              <Typography sx={{ wordBreak: "break-all" }}>
                {a.data.swaggerUrl}
              </Typography>
              <Typography variant="subtitle2" mt={2}>
                Current contract
              </Typography>
              <Typography fontFamily="monospace">
                {a.data.currentContractId
                  ? `#${a.data.currentContractId}`
                  : "Not fetched"}
              </Typography>
            </CardContent>
          </Card>
        </Grid>
        <Grid size={{ xs: 12, md: 7 }}>
          <Card>
            <CardContent>
              <Typography variant="subtitle2">
                Affected consumer APIs
              </Typography>
              <Typography variant="h3">{impacts.data?.length ?? 0}</Typography>
            </CardContent>
          </Card>
        </Grid>
      </Grid>
      <Paper>
        <Typography variant="h6" p={2}>
          Recent changes
        </Typography>
        <ChangeTable changes={c.data ?? []} />
      </Paper>
      <Paper>
        <Typography variant="h6" p={2}>
          Affected consumers
        </Typography>
        <Table size="small">
          <TableHead>
            <TableRow>
              <TableCell>Risk</TableCell>
              <TableCell>Score</TableCell>
              <TableCell>Reason</TableCell>
              <TableCell>Status</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {impacts.data?.map((i) => (
              <TableRow key={i.id}>
                <TableCell>
                  <Badge>{i.impactLevel}</Badge>
                </TableCell>
                <TableCell>{i.riskScore}</TableCell>
                <TableCell>{i.reason}</TableCell>
                <TableCell>{i.status}</TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </Paper>
    </Stack>
  );
}

function Mappings() {
  const q = useQuery<any[]>({
    queryKey: ["mappings"],
    queryFn: async () => (await api.get("/mappings")).data,
  });
  const qc = useQueryClient();
  const [file, setFile] = useState<File>();
  const upload = useMutation({
    mutationFn: async () => {
      const d = new FormData();
      d.append("file", file!);
      return api.post(
        `/mappings/import/${file!.name.endsWith(".xlsx") ? "xlsx" : "csv"}`,
        d,
      );
    },
    onSuccess: () => qc.invalidateQueries({ queryKey: ["mappings"] }),
  });
  const remove = useMutation({
    mutationFn: async (id: number) => api.delete(`/mappings/${id}`),
    onSuccess: () => qc.invalidateQueries({ queryKey: ["mappings"] }),
  });
  return (
    <Stack spacing={2}>
      <Typography variant="h4" fontWeight={800}>
        Consumer mappings
      </Typography>
      <Paper sx={{ p: 2 }}>
        <Stack direction="row" spacing={2} alignItems="center">
          <Button component="label" variant="outlined">
            Choose CSV / XLSX
            <input
              hidden
              type="file"
              accept=".csv,.xlsx"
              onChange={(e) => setFile(e.target.files?.[0])}
            />
          </Button>
          <Typography>{file?.name ?? "No file selected"}</Typography>
          <Button
            disabled={!file || upload.isPending}
            variant="contained"
            onClick={() => upload.mutate()}
          >
            Import
          </Button>
        </Stack>
        {upload.data && (
          <Alert
            sx={{ mt: 2 }}
            severity={upload.data.data.errors.length ? "warning" : "success"}
          >
            Imported {upload.data.data.imported} mappings{" "}
            {upload.data.data.errors.length
              ? `(${upload.data.data.errors.join("; ")})`
              : ""}
          </Alert>
        )}
      </Paper>
      <Paper>
        <Table size="small">
          <TableHead>
            <TableRow>
              <TableCell>Consumer</TableCell>
              <TableCell>Consumer field</TableCell>
              <TableCell>Producer</TableCell>
              <TableCell>Producer field</TableCell>
              <TableCell>Required</TableCell>
              <TableCell />
            </TableRow>
          </TableHead>
          <TableBody>
            {q.data?.map((m) => (
              <TableRow key={m.id}>
                <TableCell>
                  {m.consumerApplication}
                  <br />
                  {m.consumerMethod} {m.consumerPath}
                </TableCell>
                <TableCell sx={{ fontFamily: "monospace" }}>
                  {m.consumerFieldPath}
                </TableCell>
                <TableCell>
                  {m.producerMethod} {m.producerPath}
                </TableCell>
                <TableCell sx={{ fontFamily: "monospace" }}>
                  {m.producerFieldPath}
                </TableCell>
                <TableCell>{m.required ? "Yes" : "No"}</TableCell>
                <TableCell>
                  <IconButton onClick={() => remove.mutate(m.id)}>
                    <DeleteIcon />
                  </IconButton>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </Paper>
    </Stack>
  );
}

export default function App() {
  return (
    <Shell>
      <Routes>
        <Route path="/" element={<Dashboard />} />
        <Route path="/apis" element={<Apis />} />
        <Route path="/apis/:id" element={<ApiDetails />} />
        <Route path="/mappings" element={<Mappings />} />
      </Routes>
    </Shell>
  );
}
