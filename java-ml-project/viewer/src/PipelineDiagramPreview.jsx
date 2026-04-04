import './PipelineDiagramPreview.css'

function FlowRow({ children }) {
  return <div className="pipeline-flow-row">{children}</div>
}

function DiagramBlock({ label, layman, title }) {
  return (
    <div className="pipeline-box" title={title}>
      <span className="pipeline-box-label">{label}</span>
      <span className="pipeline-box-layman">{layman}</span>
    </div>
  )
}

function Arrow() {
  return (
    <span className="pipeline-arrow" aria-hidden="true">
      →
    </span>
  )
}

/** Visual pipeline diagram for workshop explanations. */
export default function PipelineDiagramPreview({ variant = 'full' }) {
  const showProgram = variant === 'full'

  return (
    <div className="pipeline-diagram-section">
      <h3 className="pipeline-diagram-heading">Pipeline diagram</h3>
      <p className="pipeline-diagram-note">
        {showProgram
          ? 'End-to-end flow in this program, then the five Spark ML stages chained inside the train method.'
          : 'The five Spark ML stages run in order inside the train method on SentimentModelTrainer; each stage reads and writes the columns named in the Java code.'}
      </p>

      {showProgram && (
        <>
          <p className="pipeline-diagram-subheading">Program flow</p>
          <FlowRow>
            <DiagramBlock
              label="Load"
              layman="Reads the review file into Spark so the program can use it as a table of rows."
              title="DataLoader.load"
            />
            <Arrow />
            <DiagramBlock
              label="Split"
              layman="Most rows become training data; the rest stay hidden for a fair accuracy check later."
              title="DataSplitter.split"
            />
            <Arrow />
            <DiagramBlock
              label="Train"
              layman="The model learns patterns from the training rows only, using the pipeline stages below."
              title="SentimentModelTrainer.train"
            />
            <Arrow />
            <DiagramBlock
              label="Predict"
              layman="Runs the learned pipeline on test rows to attach a guessed positive or negative label."
              title="SentimentPredictor.predict"
            />
            <Arrow />
            <DiagramBlock
              label="Evaluate"
              layman="Compares guesses to the real labels and prints scores plus a confusion matrix."
              title="ModelEvaluator.evaluate"
            />
            <Arrow />
            <DiagramBlock
              label="Samples"
              layman="Tries a few short example reviews so you can see output on brand-new sentences."
              title="SampleReviewTester.run"
            />
          </FlowRow>
        </>
      )}

      <p className="pipeline-diagram-subheading">Stages inside train</p>
      <FlowRow>
        <DiagramBlock
          label="Tokenizer"
          layman="Splits each review into separate words the next steps can count and weigh."
          title="Stage 1: text to words"
        />
        <Arrow />
        <DiagramBlock
          label="Stop words"
          layman="Removes very common words like filler that rarely signal sentiment by themselves."
          title="Stage 2: remove common words"
        />
        <Arrow />
        <DiagramBlock
          label="HashingTF"
          layman="Turns the remaining words into a numeric fingerprint of which terms show up."
          title="Stage 3: hashed term frequencies"
        />
        <Arrow />
        <DiagramBlock
          label="IDF"
          layman="Boosts unusual words and tones down words that appear everywhere so highlights matter more."
          title="Stage 4: TF-IDF features"
        />
        <Arrow />
        <DiagramBlock
          label="LinearSVC"
          layman="Learns a simple separating rule from those numbers to vote positive or negative."
          title="Stage 5: linear classifier"
        />
      </FlowRow>
    </div>
  )
}
