import Card from "../common/Card";

export default function FeedbackCard({ feedback }) {
  return (
    <Card title="피드백 메시지">
      <p className="leading-relaxed text-slate-600">
        {feedback}
      </p>
    </Card>
  );
}
